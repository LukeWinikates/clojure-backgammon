(ns backgammon.server)

(require '[clojure.java.io :as io])
(use 'ring.middleware.resource)

(defn index [x]
  (resource-request x "public"))

