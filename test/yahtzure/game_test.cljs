(ns yahtzure.game-test
  (:require [cljs.test :refer-macros [deftest is]]
            [yahtzure.state :refer [state initial-state]]
            [yahtzure.game :as game]))


(deftest test-reset-dice
  (with-redefs [state (atom {:table-dice [1 2 nil nil nil]
                             :held-dice [nil nil 3 4 5]})]
    (game/reset-dice)
    (is (= {:table-dice [nil nil nil nil nil]
            :held-dice [nil nil nil nil nil]}
           @state))))

(deftest test-dec-roll
  (with-redefs [state (atom {:rolls 3})]
    (game/dec-roll)
    (is (= {:rolls 2}
           @state))))

(deftest test-next-round
  (with-redefs [state (atom {:round 1
                             :rolls 0
                             :table-dice [1 2 nil nil nil]
                             :held-dice [nil nil 3 4 5]})]
    (game/next-round)
    (is (= {:round 2
            :rolls 3
            :table-dice [nil nil nil nil nil]
            :held-dice [nil nil nil nil nil]}
           @state))))

(deftest test-reset-game
  (with-redefs [state (atom {})]
    (game/reset-game)
    (is (= initial-state
           @state))))