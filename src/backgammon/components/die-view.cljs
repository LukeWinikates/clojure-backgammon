(ns backgammon.die-view
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put!]]))

(defn die-classes [die]
  (str "die "
       (if (:active die) "active-die" "inactive-die")
       (if (:used die) " used-die")))

(defn build [die activate]
  (dom/div #js { :onClick (fn [e] (put! activate die))
                :className (die-classes die) }
           (:value die)))
