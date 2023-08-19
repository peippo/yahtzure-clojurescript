(ns yahtzure.core
  (:require
   [reagent.dom :as d]
   [yahtzure.dice :as dice]
   [yahtzure.score :as score]))

;; -------------------------
;; Views

(defn home-page []
  [:div
   [:h1 "Yahtzure"]
   [dice/throw-button]
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
