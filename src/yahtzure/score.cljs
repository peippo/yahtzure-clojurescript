(ns yahtzure.score
  (:require [yahtzure.state :refer [state]]))

(defn all-dice []
  (filter some? (concat (:table-dice @state) (:held-dice @state))))

(defn sum-dice
  "Sum all dice or all dice with `value`"
  ([] (reduce + (all-dice)))
  ([value] (reduce + (filter #(= value %) (all-dice)))))

(defn small-straight?
  "Check if we have four sequential dice"
  []
  (let [straights [[1 2 3 4] [2 3 4 5] [3 4 5 6]]
        partitions (partition 4 1 (->> (all-dice) sort dedupe))]
    (some #(some (partial = %) partitions) straights)))

(defn large-straight?
  "Check if we have five sequential dice"
  []
  (let [straights [[1 2 3 4 5] [2 3 4 5 6]]
        partitions (partition 5 1 (->> (all-dice) sort dedupe))]
    (some #(some (partial = %) partitions) straights)))

(defn score-table []
  [:div [:p (str "aces:" (sum-dice 1))]
   [:p (str "twos:" (sum-dice 2))]
   [:p (str "threes:" (sum-dice 3))]
   [:p (str "fours:" (sum-dice 4))]
   [:p (str "fives:" (sum-dice 5))]
   [:p (str "sixes:" (sum-dice 6))]
   [:p (str "small straight:" (if (small-straight?) 30 0))]
   [:p (str "large straight:" (if (large-straight?) 40 0))]
   [:p (str "chance:" (sum-dice))]])