(ns yahtzure.score
  (:require [yahtzure.state :refer [state]]))

(defn all-dice []
  (filter some? (concat (:table-dice @state) (:held-dice @state))))

(defn sum-dices [value]
  (reduce + (filter #(= value %) (all-dice))))

(defn score-table []
  [:div [:p (str "aces:" (sum-dices 1))]
   [:p (str "twos:" (sum-dices 2))]
   [:p (str "threes:" (sum-dices 3))]
   [:p (str "fours:" (sum-dices 4))]
   [:p (str "fives:" (sum-dices 5))]
   [:p (str "sixes:" (sum-dices 6))]])