(ns backgammon.dice)

(defn roll []
  (+ 1 (rand-int 6)))

(defn pick-first-player []
  (let [left (roll)
        right (roll)
        dice [left right]]
    (cond
      (> left right) { :player :black :dice dice }
      (< left right) { :player :white :dice dice }
      :else (pick-first-player))))
