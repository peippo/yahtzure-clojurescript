(ns yahtzure.stats
  (:require [yahtzure.state :refer [state]]))

(defn log-stats!
  "Update stats map with the values of current table dice"
  []
  (swap! state
         (fn [current-state]
           (let [new-dice (:table-dice current-state)
                 stats (:stats current-state)]
             (assoc current-state
                    :stats (reduce (fn [acc die]
                                     (update acc (keyword (str die)) inc))
                                   stats
                                   (remove nil? new-dice)))))))

(defn get-polyline-points
  "Generate SVG polyline points attribute string from stats distribution"
  [width height]
  (let [stats (:stats @state)
        max-val (apply max (vals stats))
        num-points (count stats)
        step (/ width (dec num-points))]
    (->> (map-indexed
          (fn [index [_ value]]
            (let [normalized-val (if (zero? max-val) 0 (/ value max-val))
                  y-val (* height (- 1 normalized-val))]
              (str (* index step) "," y-val)))
          stats)
         (interpose " ")
         (apply str))))

;; -------------------------
;; UI elements

(defn stats-graph []
  [:div {:class "bg-gradient-to-t from-slate-700/25 to-transparent p-5 rounded-lg"}
   (let [width 400
         height 70]
     [:svg {:width "100%" :height "100%" :class "p-5" :viewBox (str "0 0 " width " " height)}
      [:polyline {:points (get-polyline-points width height)
                  :class "stroke-emerald-500 fill-transparent"}]])
   (let [tick-classes "relative after:content-[''] after:absolute after:left-1/2 after:-top-4 after:w-[1px] after:h-2 after:bg-slate-500"]
     [:div {:class "flex justify-between text-xs md:text-sm text-slate-500 mt-2"}
      [:div {:class tick-classes} [:p "Aces"]]
      [:div {:class tick-classes} [:p "Twos"]]
      [:div {:class tick-classes} [:p "Threes"]]
      [:div {:class tick-classes} [:p "Fours"]]
      [:div {:class tick-classes} [:p "Fives"]]
      [:div {:class tick-classes} [:p "Sixes"]]])])