(ns yahtzure.game
  (:require [yahtzure.state :refer [state initial-state]]))

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

(defn reset-game
  "Reset game state"
  []
  (reset! state initial-state))

;; -------------------------
;; UI elements

(defn round-counter []
  (let [current-round (:round @state)]
    [:div
     [:p (if (<= current-round 13)
           (str "Round: " current-round)
           (str "Game finished!"))]]))

(defn reset-button []
  [:button {:on-click #(reset-game)
            :class "bg-emerald-500 text-slate-900 hover:bg-emerald-300 hover:cursor-pointer p-3"}
   "New game"])