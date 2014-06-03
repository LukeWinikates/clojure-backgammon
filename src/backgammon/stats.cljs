(ns backgammon.stats)

(defn pip-score [board]
  (let [pips (:pips board)
    black-distance
     (reduce
       (fn [acc, pip]
         (if (= (:owner pip) :black)
           (+ (* (:index pip) (:count pip)) acc)
           acc))
       0
       pips)]
    {:black black-distance}))
