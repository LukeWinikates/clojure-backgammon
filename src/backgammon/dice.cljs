(ns backgammon.dice)

(defn roll-one []
  (+ 1 (rand-int 6)))

(defn die [value]
  { :value value :used false })

(defn activate
  ([dice] (activate
            dice
            (first (filter (fn [d] (not (:used d))) dice))))
  ([dice new-active-die]
   (map
     (fn [d] (merge d {:active (identical? d new-active-die)}))
     dice)))

(defn pick-first-player []
  (let [left (roll-one)
        right (roll-one)
        dice (activate (map die [left right]))]
    (cond
      (> left right) { :player :black :dice dice }
      (< left right) { :player :white :dice dice }
      :else (pick-first-player))))

(defn use-die [die dice]
  (map (fn [d] (merge d {:used (or (:used d) (identical? die d)) })) dice))

(defn active [dice]
  (first (filter (fn [d] (:active d)) dice)))

(defn all-used? [dice]
  (every?
    (fn [d] (:used d))
    dice))

(defn roll-dice [dice]
  (activate (map die [(roll-one) (roll-one)])))

(defn roll
  [board]
  (merge board
         { :player (swap-player (:player board))
           :dice (roll-dice (:dice board)) }))

(defn swap-player [player]
  (if
    (= player :black)
    :white
    :black))

(defn unused? [die]
  (and (not (nil? die))
       (not (:used die))))
