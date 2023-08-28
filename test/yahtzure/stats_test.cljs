(ns yahtzure.stats-test
  (:require [cljs.test :refer-macros [deftest is]]
            [yahtzure.state :refer [state]]
            [yahtzure.stats :as stats]))

(deftest test-log-stats!
  []
  (with-redefs [state (atom {:table-dice [nil nil nil 6 6]
                             :stats {:1 0 :2 0 :3 0 :4 0 :5 0 :6 0}})]
    (stats/log-stats!)
    (is (= {:1 0 :2 0 :3 0 :4 0 :5 0 :6 2} (:stats @state))))

  (with-redefs [state (atom {:table-dice [1 2 2 3 5]
                             :stats {:1 0 :2 0 :3 0 :4 0 :5 0 :6 0}})]
    (stats/log-stats!)
    (is (= {:1 1 :2 2 :3 1 :4 0 :5 1 :6 0} (:stats @state)))))

(deftest test-get-polyline-points
  []
  (with-redefs [state (atom {:stats {:1 0 :2 1 :3 1 :4 0 :5 1 :6 2}})]
    (is (= "0,80 80,40 160,40 240,80 320,40 400,0" (stats/get-polyline-points 400 80))))

  (with-redefs [state (atom {:stats {:1 2 :2 0 :3 1 :4 0 :5 0 :6 2}})]
    (is (= "0,0 80,80 160,40 240,80 320,80 400,0" (stats/get-polyline-points 400 80)))))