(ns yahtzure.core
    (:require
     [reagent.core :as r]
     [reagent.dom :as d]
     [yahtzure.dice :as dice]))

;; -------------------------
;; Views

(defn home-page []
  [:div 
   [:h1 "Yahtzure"]
   [dice/throw-button]
   [dice/table-dice]
   [dice/held-dice]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
