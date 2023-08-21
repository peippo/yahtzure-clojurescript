(ns yahtzure.dice
  (:require [yahtzure.state :refer [state]]
            [yahtzure.game :as game]))

(defn roll-die [] (inc (rand-int 6)))
(defn roll-dice [count] (repeatedly count roll-die))

(defn hold-die
  "Move selected die from table to the held state"
  [index]
  (let [die-value (nth (:table-dice @state) index)]
    (swap! state
           (fn [state]
             (-> state
                 (update :table-dice assoc index nil)
                 (update :held-dice assoc index die-value))))))

(defn return-die
  "Return selected die from held state to the table"
  [index]
  (let [die-value (nth (:held-dice @state) index)]
    (swap! state
           (fn [state]
             (-> state
                 (update :table-dice assoc index die-value)
                 (update :held-dice assoc index nil))))))

(defn roll-dice!
  "Roll 5 dice if the table is empty, otherwise reroll table dice"
  [old-state]
  (let [held-count (count (filter some? (:held-dice old-state)))
        dice-indices (keep-indexed
                      (fn [index value]
                        (when (or (not (nil? value))
                                  (and (empty? (filter some? (:table-dice old-state))) (< held-count 5)))
                          index))
                      (:table-dice old-state))
        rolls (vec (roll-dice (count dice-indices)))]
    (reduce
     (fn [state [index roll]]
       (assoc-in state [:table-dice index] roll))
     old-state
     (map vector dice-indices rolls))))

(defn table-dice []
  [:ul
   (for [[index value] (map-indexed vector (:table-dice @state))]
     (when (not (nil? value))
       ^{:key (str index "-" value)}
       [:li
        [:button {:on-click #(hold-die index)} (str value)]]))])

(defn held-dice []
  [:ul
   (for [[index value] (map-indexed vector (:held-dice @state))]
     (when (not (nil? value))
       ^{:key (str index "-" value)}
       [:li
        [:button {:on-click #(return-die index)} (str value)]]))])

(defn roll-button []
  (let [held-count (count (filter some? (:held-dice @state)))]
    [:input {:type "button"
             :value (if (= 3 (:rolls @state)) "Roll" "Re-roll")
             :disabled (or (= 5 held-count) (= 0 (:rolls @state)))
             :on-click #(do ((swap! state roll-dice!)
                             (game/dec-roll)))}]))