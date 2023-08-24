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

(defn all-dice []
  (filter some? (concat (:table-dice @state) (:held-dice @state))))

(defn sum-dice
  "Sum all dice or all dice with `value`"
  ([] (reduce + (all-dice)))
  ([value] (reduce + (filter #(= value %) (all-dice)))))

(defn partition-dice-by-value
  [] (->> (all-dice)
          (sort)
          (partition-by identity)
          (sort-by count)))

(defn has-n-of-kind? [n]
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

(defn lock-score [name score]
  (swap! state assoc-in [:scores name] {:score score :locked true})
  (game/next-round))

;; -------------------------
;; UI elements

(defn score-row [name data calculated-score]
  (let [{:keys [score locked]} data]
    [:div {:class "table-row"}
     [:div {:class "table-cell text-right border-b border-emerald-800 py-2 px-3"}
      (if (true? locked)
        [:p {:class "text-emerald-200"} (get combinations name)]
        [:p (get combinations name)])]
     [:div {:class "table-cell border-b border-emerald-800"}
      (if (or (true? locked)
              (= 3 (:rolls @state)))
        [:button {:class (str "w-full h-full py-2 px-3" (if locked " text-emerald-200" "")) :disabled true} (str score)]
        [:button {:class "bg-emerald-500 text-slate-900 hover:bg-emerald-300 hover:cursor-pointer w-full h-full py-2 px-3"
                  :on-click #(lock-score name calculated-score)} calculated-score])]]))

(defn score-table []
  (let [score-state (:scores @state)]
    [:div {:class "table"}
     [score-row :aces (:aces score-state) (sum-dice 1)]
     [score-row :twos (:twos score-state) (sum-dice 2)]
     [score-row :threes (:threes score-state) (sum-dice 3)]
     [score-row :fours (:fours score-state) (sum-dice 4)]
     [score-row :fives (:fives score-state) (sum-dice 5)]
     [score-row :sixes (:sixes score-state) (sum-dice 6)]
     [score-row :three-of-a-kind (:three-of-a-kind score-state) (if (three-of-a-kind?) (sum-dice) 0)]
     [score-row :four-of-a-kind (:four-of-a-kind score-state) (if (four-of-a-kind?) (sum-dice) 0)]
     [score-row :full-house (:full-house score-state) (if (full-house?) 25 0)]
     [score-row :small-straight (:small-straight score-state) (if (small-straight?) 30 0)]
     [score-row :large-straight (:large-straight score-state) (if (large-straight?) 40 0)]
     [score-row :yahtzee (:yahtzee score-state) (if (yahtzee?) 50 0)]
     [score-row :chance (:chance score-state) (sum-dice)]]))
