(ns backgammon.components.stats-view
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [backgammon.stats :as stats]))

(defn build
  [board]
  (dom/div
    #js{ :className "stats" }
    (dom/h4 nil "Stats")
    (dom/table
      nil
      (dom/thead
        nil
        (dom/th nil)
        (dom/th nil "Black")
        (dom/th nil "White")
      (dom/tbody
        nil
        (dom/tr
          nil
          (dom/td nil "Pips")
          (dom/td nil (:black (stats/pip-score board)))
          (dom/td nil 10)))))))
