(ns backgammon.stats)

(defn distance-from-home [idx, color]
  (condp = color
    :black idx
    :white (- 25 idx)))

(defn pip-score [board]
  (let [pips (:pips board)]
    (reduce
      (fn [acc, pip]
        (let [owner (:owner pip)
              old-val (get acc owner)]
          (if-not (nil? (:owner pip))
            (assoc acc owner (+ old-val (* (distance-from-home (:index pip) owner) (:count pip))))
            acc)))
      {:black 0 :white 0 }
      pips)))
