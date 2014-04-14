(ns backgammon.dice)

(defn roll []
  (+ 1 (rand-int 6)))

(defn die [value]
  { :value value :used false })

(defn activate
  ([dice] (activate dice (first dice)))
  ([dice new-active-die]
   (map
     (fn [d] (merge d {:active (= d new-active-die)}))
     dice)))

(defn pick-first-player []
  (let [left (roll)
        right (roll)
        dice (activate (map die [left right]))]
    (cond
      (> left right) { :player :black :dice dice }
      (< left right) { :player :white :dice dice }
      :else (pick-first-player))))


