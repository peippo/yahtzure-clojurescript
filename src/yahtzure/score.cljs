(ns yahtzure.score
  (:require [yahtzure.state :refer [state]]
            [yahtzure.game :as game]))

(def combinations {:aces "Aces"
                   :twos "Twos"
                   :threes "Threes"
                   :fours "Fours"
                   :fives "Fives"
                   :sixes "Sixes"
                   :three-of-a-kind "Three-of-a-kind"
                   :four-of-a-kind "Four-of-a-kind"
                   :full-house "Full house"
                   :small-straight "Small straight"
                   :large-straight "Large straight"
                   :yahtzee "Yahtzee!"
                   :chance "Chance"})

(defn all-dice
  "Return all the dice in play (table and held)"
  []
  (filter some? (concat (:table-dice @state) (:held-dice @state))))

(defn sum-dice
  "Sum all dice or all dice with `value`"
  ([] (reduce + (all-dice)))
  ([value] (reduce + (filter #(= value %) (all-dice)))))

(defn sum-upper-section
  "Sum score table upper section"
  []
  (let [upper-section [:aces :twos :threes :fours :fives :sixes]
        score-table (:scores @state)]
    (reduce (fn [acc key]
              (+ acc (:score (get score-table key 0))))
            0
            upper-section)))

(defn partition-dice-by-value
  "Group dice by value, and sort the groups by count"
  [] (->> (all-dice)
          (sort)
          (partition-by identity)
          (sort-by count)))

(defn has-n-of-kind?
  "Check if there are `n` of any value dice"
  [n]
  (some #(>= (count %) n) (partition-dice-by-value)))

(defn three-of-a-kind?
  "Check if we have three of a kind"
  []
  (has-n-of-kind? 3))

(defn four-of-a-kind?
  "Check if we have four of a kind"
  []
  (has-n-of-kind? 4))

(defn yahtzee?
  "Check if we have a yahtzee"
  []
  (has-n-of-kind? 5))

(defn full-house?
  "Check if we have three of a kind and a pair"
  []
  (or (and (= 2 (count (first (partition-dice-by-value))))
           (= 3 (count (last  (partition-dice-by-value)))))
      (has-n-of-kind? 5)))

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

(defn upper-section-full?
  "Check if all upper section scores are locked"
  []
  (let [upper-section [:aces :twos :threes :fours :fives :sixes]
        score-table (:scores @state)]
    (every? (fn [key]
              (:locked (get score-table key)))
            upper-section)))

(defn upper-bonus?
  "Check if we have 63 or more points in the score table upper section"
  []
  (<= 63 (sum-upper-section)))

(defn lock-score
  "Add `score` to `name` combination and lock the row"
  [name score]
  (swap! state assoc-in [:scores name] {:score score :locked true}))

;; -------------------------
;; UI elements

(defn score-row [name data calculated-score]
  (let [{:keys [score locked]} data]
    [:div {:class "table-row"}
     [:div {:class "table-cell text-right border-b border-emerald-800 py-2 px-3"}
      (if (true? locked)
        [:p {:class "text-emerald-200"} (get combinations name)]
        [:p (get combinations name)])]
     [:div {:class "table-cell w-20 text-center border-b border-emerald-800"}
      (if (or (true? locked)
              (= 3 (:rolls @state)))
        [:button {:class (str "py-2" (if locked " text-emerald-200" "")) :disabled true} (if (and (= score 0) locked)  "-" (str score))]
        [:button {:class "w-full h-full bg-emerald-500 text-slate-900 hover:bg-emerald-300 hover:cursor-pointer ease-out duration-200 py-2 px-3"
                  :on-click #(do (lock-score name calculated-score)
                                 (game/next-round))}
         (if (= calculated-score 0)  "-" (str calculated-score))])]]))

(defn upper-bonus-row []
  [:div {:class "table-row bg-slate-900"}
   [:div {:class "table-cell text-right border-b border-emerald-800 py-2 px-3"}
    [:p {:class "text-slate-500"} "Bonus"]]
   [:div {:class "table-cell border-b border-emerald-800"}
    (when (< 0 (sum-upper-section))
      [:p {:class "text-center py-2 px-3"}
       (if (upper-bonus?)
         [:span {:class "text-emerald-200"} 35]
         (if (upper-section-full?)
           [:span {:class "text-slate-500"} "-"]
           [:span {:class "text-slate-500"} (str "-" (- 63 (sum-upper-section)))]))])]])

(defn score-table []
  (let [score-state (:scores @state)]
    [:div {:class "table"}
     [score-row :aces (:aces score-state) (sum-dice 1)]
     [score-row :twos (:twos score-state) (sum-dice 2)]
     [score-row :threes (:threes score-state) (sum-dice 3)]
     [score-row :fours (:fours score-state) (sum-dice 4)]
     [score-row :fives (:fives score-state) (sum-dice 5)]
     [score-row :sixes (:sixes score-state) (sum-dice 6)]
     [upper-bonus-row]
     [score-row :three-of-a-kind (:three-of-a-kind score-state) (if (three-of-a-kind?) (sum-dice) 0)]
     [score-row :four-of-a-kind (:four-of-a-kind score-state) (if (four-of-a-kind?) (sum-dice) 0)]
     [score-row :full-house (:full-house score-state) (if (full-house?) 25 0)]
     [score-row :small-straight (:small-straight score-state) (if (small-straight?) 30 0)]
     [score-row :large-straight (:large-straight score-state) (if (large-straight?) 40 0)]
     [score-row :yahtzee (:yahtzee score-state) (if (yahtzee?) 50 0)]
     [score-row :chance (:chance score-state) (sum-dice)]]))
