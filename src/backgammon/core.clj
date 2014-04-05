(ns backgammon.core)

(require '[clojure.java.io :as io])
(use 'ring.middleware.resource)

(defn index [x]
  (resource-request x "public"))

