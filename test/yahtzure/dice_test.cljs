(ns yahtzure.dice-test
  (:require [cljs.test :refer-macros [deftest is]]
            [yahtzure.state :refer [state]]
            [yahtzure.dice :as dice]))

(deftest test-roll-die
  (is (every? #(<= 1 % 6) (repeatedly 100 dice/roll-die))))

(deftest test-roll-dice
  (is (= 5 (count (dice/roll-dice 5))))
  (is (= 0 (count (dice/roll-dice 0))))

  (with-redefs [dice/roll-die (constantly 3)]
    (is (= [3 3 3] (dice/roll-dice 3)))))

(deftest test-hold-die
  (with-redefs [state (atom {:table-dice [1 2 3 4 5]
                             :held-dice [nil nil nil nil nil]})]
    (dice/hold-die 0)
    (is (= {:table-dice [nil 2 3 4 5]
            :held-dice [1 nil nil nil nil]
            :animate-spin false}
           @state))))

(deftest test-return-die
  (with-redefs [state (atom {:table-dice [nil 2 3 4 5]
                             :held-dice [1 nil nil nil nil]})]
    (dice/return-die 0)
    (is (= {:table-dice [1 2 3 4 5]
            :held-dice [nil nil nil nil nil]
            :animate-spin false}
           @state))))

(deftest test-roll-dice!
  ; empty table
  (with-redefs [state (atom {:table-dice [nil nil nil nil nil]
                             :held-dice [nil nil nil nil nil]})]
    (swap! state dice/roll-dice!)
    (is (= 5 (count (:table-dice @state)))))

  ; with held dice
  (with-redefs [state (atom {:table-dice [nil nil 3 4 5]
                             :held-dice [1 2 nil nil nil]})]
    (swap! state dice/roll-dice!)
    (let [table-dice (:table-dice @state)
          held-dice (:held-dice @state)]
      (is (= 3 (count (remove nil? table-dice))))
      (is (= 2 (count (remove nil? held-dice)))))))
