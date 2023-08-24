(ns yahtzure.game
  (:require [yahtzure.state :refer [state]]))

(defn reset-dice
  "Remove all dice from table & hold"
  []
  (swap! state assoc
         :table-dice [nil nil nil nil nil]
         :held-dice [nil nil nil nil nil]))

(defn dec-roll
  "Decrement roll counter"
  []
  (swap! state update :rolls dec))

(defn next-round
  "Reset table, end current round, and move to the next"
  []
  (reset-dice)
  (swap! state update :round inc)
  (swap! state assoc :rolls 3))

;; -------------------------
;; UI elements

(defn round-counter []
  [:div
   [:p (str "Round: " (:round @state))]])