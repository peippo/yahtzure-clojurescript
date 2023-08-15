(ns yahtzure.dice
  (:require [reagent.core :as r]))

(defn roll-die [] (inc (rand-int 6)))
(defn roll-dice [count] (repeatedly count roll-die))

(def dice-values (r/atom ()))

(defn dice []
  [:ul
   (for [[index value] (map-indexed vector @dice-values)]
     ^{:key (str index "-" value)} 
     [:li value])])

(defn dice-area []
  [:div
   [:input {:type "button"
            :value "Throw"
            :on-click #(reset! dice-values (vec (roll-dice 6)))}]
   [dice]])