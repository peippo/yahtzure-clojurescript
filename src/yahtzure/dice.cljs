(ns yahtzure.dice
  (:require [yahtzure.state :refer [state]]
            [yahtzure.game :as game]
            [yahtzure.stats :as stats]))

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

;; -------------------------
;; UI elements

(def dice-classes (str "relative w-full h-full aspect-square bg-gradient-to-t from-slate-600 to-slate-400 text-slate-900 text-xl xs:text-2xl sm:text-4xl rounded-lg drop-shadow-md "
                       "after:content-[''] after:absolute after:w-full after:h-full after:rounded-[2rem] after:inset-0 after:bg-gradient-to-b after:from-white after:to-slate-300 "
                       "hover:scale-105 ease-out duration-100"))
(def dice-slot-classes "w-full h-full aspect-square border-2 border-dashed border-emerald-800 rounded-md p-1 sm:p-2")

(defn table-dice []
  [:ul {:class "grid grid-cols-5 gap-2"}
   (for [[index value] (map-indexed vector (:table-dice @state))]
     ^{:key (str index "-" (or value "nil"))}
     [:li {:class dice-slot-classes}
      (when (not (nil? value))
        [:button {:class dice-classes :on-click #(hold-die index)} [:span {:class "relative z-10"} (str value)]])])])

(defn held-dice []
  [:ul {:class "grid grid-cols-5 gap-2"}
   (for [[index value] (map-indexed vector (:held-dice @state))]
     ^{:key (str index "-" (or value "nil"))}
     [:li {:class dice-slot-classes}
      (when (not (nil? value))
        [:button {:class dice-classes :on-click #(return-die index)} [:span {:class "relative z-10"} (str value)]])])])

(defn roll-button []
  (let [held-count (count (filter some? (:held-dice @state)))
        disabled (or (= 5 held-count) (= 0 (:rolls @state)))]
    [:input {:type "button"
             :class (str (if disabled
                           "bg-slate-600 text-slate-800 hover:cursor-not-allowed p-3"
                           "bg-emerald-500 text-slate-900 hover:bg-emerald-300 hover:cursor-pointer ease-out duration-200 p-3"))
             :value (if (= 3 (:rolls @state)) "Roll" (str "Re-roll (" (:rolls @state) ")"))
             :disabled disabled
             :on-click #(do (swap! state roll-dice!)
                            (game/dec-roll)
                            (stats/log-stats!))}]))