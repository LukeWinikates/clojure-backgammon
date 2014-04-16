(ns backgammon.gutter)

(defn capture [gutters player]
  (let [gutter (get gutters player)
        new-count (+ 1 (:count gutter))]
    (merge gutters { player { :count new-count } })))
