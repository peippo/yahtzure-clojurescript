(ns yahtzure.state
  (:require [reagent.core :as r]))

(defonce state (r/atom {:table-dice [nil nil nil nil nil]
                        :held-dice [nil nil nil nil nil]}))