(ns yahtzure.state
  (:require [reagent.core :as r]))

(def initial-state {:round 1
                    :rolls 3
                    :table-dice [nil nil nil nil nil]
                    :held-dice [nil nil nil nil nil]
                    :stats {:1 0 :2 0 :3 0 :4 0 :5 0 :6 0}
                    :scores {:aces {:score 0 :locked false}
                             :twos {:score 0 :locked false}
                             :threes {:score 0 :locked false}
                             :fours {:score 0 :locked false}
                             :fives {:score 0 :locked false}
                             :sixes {:score 0 :locked false}
                             :upper-bonus {:score 0 :locked false}
                             :three-of-a-kind {:score 0 :locked false}
                             :four-of-a-kind {:score 0 :locked false}
                             :full-house {:score 0 :locked false}
                             :small-straight {:score 0 :locked false}
                             :large-straight {:score 0 :locked false}
                             :yahtzee {:score 0 :locked false}
                             :chance {:score 0 :locked false}}
                    :animate-spin true})

(defonce state (r/atom initial-state))