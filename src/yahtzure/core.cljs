(ns yahtzure.core
  (:require
   [reagent.dom :as d]
   [yahtzure.dice :as dice]
   [yahtzure.score :as score]
   [yahtzure.game :as game]))

;; -------------------------
;; Views

(defn home-page []
  [:div
   [:h1 "Yahtzure"]
   [game/round-counter]
   [game/roll-counter]
   [dice/roll-button]
   [:div {:style {:display "flex" :gap "2rem"}}
    [:div [:p>strong "Table"]
     [dice/table-dice]
     [:p>strong "Hold"]
     [dice/held-dice]]
    [:div
     [:p>strong "Scores"]
     [score/score-table]]]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
