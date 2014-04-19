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

(defn from-values [values]
  (activate (map die values)))

(defn pick-first-player []
  (let [left  (roll-one)
        right (roll-one)
        dice (activate (map die [left right]))]
    (cond
      (> left right) { :player :black :dice dice }
      (< left right) { :player :white :dice dice }
      :else (pick-first-player))))

(defn use-die [die dice]
  (map (fn [d]
         (let [is-used? (or (:used d) (identical? die d))]
           (merge d {:used is-used? })))
         dice))

(defn active [dice]
  (first (filter (fn [d] (:active d)) dice)))

(defn all-used? [dice]
  (every?
    (fn [d] (:used d))
    dice))

(defn roll-dice []
  (let [[a b] [(roll-one) (roll-one)]
        dice (if (= a b)
               [a a a a]
               [a b])]
  (activate (map die dice))))

(defn swap-player [player]
  (if
    (= player :black)
    :white
    :black))

(defn roll
  [board]
  (merge board
         { :player (swap-player (:player board))
           :dice (roll-dice) }))

(defn unused? [die]
  (and (not (nil? die))
       (not (:used die))))
