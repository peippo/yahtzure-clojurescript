(ns yahtzure.score-test
  (:require [cljs.test :refer-macros [deftest is]]
            [yahtzure.state :refer [state]]
            [yahtzure.score :as score]))

(deftest test-all-dice
  (with-redefs [state (atom {:table-dice [1 2 3 nil nil]
                             :held-dice [nil nil nil 4 5]})]
    (is (= [1 2 3 4 5] (score/all-dice)))))

(deftest test-sum-dice
  (with-redefs [state (atom {:table-dice [1 2 3 nil nil]
                             :held-dice [nil nil nil 4 nil]})]
    (is (= 10 (score/sum-dice)))))

(deftest test-sum-upper-section
  (with-redefs [state (atom {:scores {:aces {:score 3 :locked true}
                                      :twos {:score 6 :locked true}
                                      :threes {:score 9 :locked true}
                                      :fours {:score 12 :locked true}
                                      :fives {:score 15 :locked true}
                                      :sixes {:score 18 :locked true}}})]
    (is (= 63 (score/sum-upper-section)))))

(deftest test-sum-total-score
  (with-redefs [state (atom {:scores {:aces {:score 3 :locked true}
                                      :twos {:score 6 :locked true}
                                      :threes {:score 9 :locked true}
                                      :fours {:score 12 :locked true}
                                      :fives {:score 15 :locked true}
                                      :sixes {:score 18 :locked true}
                                      :upper-bonus {:score 35 :locked true}
                                      :three-of-a-kind {:score 30 :locked true}
                                      :four-of-a-kind {:score 30 :locked true}
                                      :full-house {:score 25 :locked true}
                                      :small-straight {:score 30 :locked true}
                                      :large-straight {:score 40 :locked true}
                                      :yahtzee {:score 50 :locked true}
                                      :chance {:score 30 :locked true}}})]
    (is (= 333 (score/sum-total-score)))))

(deftest test-partition-dice-by-value
  (with-redefs [state (atom {:table-dice [1 1 2 nil nil]
                             :held-dice [nil nil nil 2 2]})]
    (println (score/partition-dice-by-value))
    (is (= '((1 1) (2 2 2)) (score/partition-dice-by-value))))

  (with-redefs [state (atom {:table-dice [6 3 nil nil 5]
                             :held-dice [nil nil 3 2 nil]})]
    (println (score/partition-dice-by-value))
    (is (= '((2) (5) (6) (3 3)) (score/partition-dice-by-value)))))

(deftest test-has-n-of-kind?
  (with-redefs [state (atom {:table-dice [1 2 nil nil nil]
                             :held-dice [nil nil 3 4 4]})]
    (is (= nil (score/has-n-of-kind? 3))))

  (with-redefs [state (atom {:table-dice [6 6 nil nil nil]
                             :held-dice [nil nil 6 6 6]})]
    (is (= true (score/has-n-of-kind? 5)))))

(deftest test-full-house?
  (with-redefs [state (atom {:table-dice [1 2 nil nil nil]
                             :held-dice [nil nil 3 4 4]})]
    (is (= nil (score/full-house?))))

  (with-redefs [state (atom {:table-dice [1 1 nil nil nil]
                             :held-dice [nil nil 2 2 2]})]
    (is (= true (score/full-house?))))

  (with-redefs [state (atom {:table-dice [6 6 nil nil nil]
                             :held-dice [nil nil 6 6 6]})]
    (is (= true (score/full-house?)))))

(deftest test-small-straight?
  (with-redefs [state (atom {:table-dice [2 2 nil nil nil]
                             :held-dice [nil nil 3 4 4]})]
    (is (= nil (score/small-straight?))))

  (with-redefs [state (atom {:table-dice [1 2 nil nil nil]
                             :held-dice [nil nil 3 4 6]})]
    (is (= true (score/small-straight?))))

  (with-redefs [state (atom {:table-dice [2 3 4 5 5]
                             :held-dice [nil nil nil nil nil]})]
    (is (= true (score/small-straight?)))))

(deftest test-large-straight?
  (with-redefs [state (atom {:table-dice [2 2 nil nil nil]
                             :held-dice [nil nil 3 4 4]})]
    (is (= nil (score/large-straight?))))

  (with-redefs [state (atom {:table-dice [1 2 nil nil nil]
                             :held-dice [nil nil 3 4 5]})]
    (is (= true (score/large-straight?))))

  (with-redefs [state (atom {:table-dice [2 3 4 5 6]
                             :held-dice [nil nil nil nil nil]})]
    (is (= true (score/large-straight?)))))

(deftest test-upper-section-full?
  (with-redefs [state (atom {:scores {:aces {:score 3 :locked true}
                                      :twos {:score 6 :locked true}
                                      :threes {:score 0 :locked false}
                                      :fours {:score 0 :locked false}
                                      :fives {:score 0 :locked false}
                                      :sixes {:score 0 :locked false}}})]
    (is (= false (score/upper-section-full?))))

  (with-redefs [state (atom {:scores {:aces {:score 3 :locked true}
                                      :twos {:score 6 :locked true}
                                      :threes {:score 9 :locked true}
                                      :fours {:score 12 :locked true}
                                      :fives {:score 15 :locked true}
                                      :sixes {:score 18 :locked true}}})]
    (is (= true (score/upper-section-full?)))))

(deftest test-upper-bonus?
  (with-redefs [state (atom {:scores {:aces {:score 1 :locked true}
                                      :twos {:score 2 :locked true}
                                      :threes {:score 0 :locked false}
                                      :fours {:score 0 :locked false}
                                      :fives {:score 0 :locked false}
                                      :sixes {:score 0 :locked false}}})]
    (is (= false (score/upper-bonus?))))

  (with-redefs [state (atom {:scores {:aces {:score 1 :locked true}
                                      :twos {:score 2 :locked true}
                                      :threes {:score 3 :locked true}
                                      :fours {:score 4 :locked true}
                                      :fives {:score 5 :locked true}
                                      :sixes {:score 6 :locked true}}})]
    (is (= false (score/upper-bonus?))))

  (with-redefs [state (atom {:scores {:aces {:score 3 :locked true}
                                      :twos {:score 6 :locked true}
                                      :threes {:score 9 :locked true}
                                      :fours {:score 12 :locked true}
                                      :fives {:score 15 :locked true}
                                      :sixes {:score 18 :locked true}}})]
    (is (= true (score/upper-bonus?))))

  (with-redefs [state (atom {:scores {:aces {:score 5 :locked true}
                                      :twos {:score 10 :locked true}
                                      :threes {:score 15 :locked true}
                                      :fours {:score 20 :locked true}
                                      :fives {:score 25 :locked true}
                                      :sixes {:score 30 :locked true}}})]
    (is (= true (score/upper-bonus?)))))

(deftest test-lock-score
  (with-redefs [state (atom {:scores {:aces {:score 3 :locked true}
                                      :twos {:score 6 :locked true}
                                      :threes {:score 0 :locked false}
                                      :fours {:score 0 :locked false}
                                      :fives {:score 0 :locked false}
                                      :sixes {:score 0 :locked false}}})]
    (score/lock-score :threes 9)
    (is (= {:scores {:aces {:score 3 :locked true}
                     :twos {:score 6 :locked true}
                     :threes {:score 9 :locked true}
                     :fours {:score 0 :locked false}
                     :fives {:score 0 :locked false}
                     :sixes {:score 0 :locked false}}}
           @state))))