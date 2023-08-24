(ns yahtzure.core
  (:require
   [reagent.dom :as d]
   [yahtzure.dice :as dice]
   [yahtzure.score :as score]
   [yahtzure.game :as game]))

;; -------------------------
;; Views

(defn home-page []
  [:<>
   [:header {:class "flex items-center justify-between gap-4 bg-slate-600 p-2 px-8"}
    [:h1 {:class "text-xl text-emerald-50 font-bold"} "Yahtzure"]
    [:p {:class "text-sm text-emerald-300 ml-0 mr-auto"} "Yahtzee w/ Clojure"]
    [game/round-counter]]
   [:main {:class "max-w-6xl p-8 mx-auto"}
    [:div {:class "flex flex-col lg:flex-row justify-between gap-16"}
     [:div {:class "flex flex-col lg:w-2/3"}
      [dice/roll-button]
      [:section {:class "mt-5"}
       [:h2 {:class "mb-3"} "Table"]
       [dice/table-dice]]
      [:section {:class "mt-5"}
       [:h2 {:class "mb-3"} "Hold"]
       [dice/held-dice]]]
     [:section {:class "flex flex-col lg:w-1/3"}
      [:h2 {:class "border-b-2 border-emerald-400 py-2 mt-1"} "Scores"]
      [score/score-table]]]]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
