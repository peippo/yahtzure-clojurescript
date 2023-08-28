(ns yahtzure.core
  (:require
   [reagent.dom :as d]
   [yahtzure.dice :as dice]
   [yahtzure.score :as score]
   [yahtzure.game :as game]
   [yahtzure.stats :as stats]
   [yahtzure.state :refer [state]]))

(defn game []
  [:<>
   [:header {:class "flex items-center justify-between gap-4 bg-slate-600 p-2 px-8"}
    [:h1 {:class "text-xl text-emerald-50 font-bold"} "Yahtzure"]
    [:p {:class "hidden md:inline-block text-sm text-emerald-300 ml-0 mr-auto"} "Yahtzee w/ Clojure"]
    [game/round-counter]]
   [:main {:class "max-w-6xl p-4 md:p-8 mx-auto"}
    [:div {:class "flex flex-col lg:flex-row justify-between gap-8 lg:gap-16"}
     [:div {:class "flex flex-col lg:w-2/3"}
      (if (= 14 (:round @state))
        [game/reset-button]
        [dice/roll-button])
      [:section {:class "mt-5"}
       [:h2 {:class "mb-3"} "Table"]
       [dice/table-dice]]
      [:section {:class "mt-5"}
       [:h2 {:class "mb-3"} "Hold"]
       [dice/held-dice]]
      [:section {:class "mt-10"}
       [:h2 {:class "text-slate-500"} "Relative dice distribution"]
       [stats/stats-graph]]]
     [:section {:class "flex flex-col lg:w-1/3"}
      [:h2 {:class "border-b-2 border-emerald-400 py-2 mt-1"} "Scores"]
      [score/score-table]]]]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [game] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
