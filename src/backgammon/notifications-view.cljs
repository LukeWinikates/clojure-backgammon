(ns backgammon.notifications-view
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [backgammon.notifications :as notf]
            [clojure.string]
            [cljs.core.async :refer [put!]]))

(defn notifications-view [notifications spin-notification]
  (dom/div
    #js{:className "notifications bordered"}
    (dom/div
      #js{:className "notification"}
      (dom/span nil (:text (:current notifications)))
      (dom/span
        #js{:className "spinners pull-right noselect"}
        (if (notf/has-prev? notifications)
          (dom/span
            #js{:onClick (fn [e] (put! spin-notification -))}
            "<"))
        (if (notf/has-next? notifications)
          (dom/span
            #js{:onClick (fn [e] (put! spin-notification +))}
            ">"))))))
