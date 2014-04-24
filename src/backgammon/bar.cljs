(ns backgammon.bar)

(defn capture [bars player]
  (let [bar (get bars player)
        new-count (+ 1 (:count bar))]
    (merge bars { player { :count new-count :owner player } })))

(defn new-bars []
  { :black {:count 0}
   :white {:count 0 }})
