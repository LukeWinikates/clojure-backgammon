(ns backgammon.rules)

(defn with-captured-checkers [board move]
  (let [player (:player board)
        player-bar (player (:bars board))
        is-captured? (> (:count player-bar) 0)]
    (or (not is-captured?)
        (= (:source move) player-bar))))

(defn belongs-to? [pip player]
  (= (:owner pip) player))

(defn none? [f coll]
  (reduce
    (fn [memo item]
      (and memo (not (f item))))
    true
    coll))

(defn home [board player]
  (condp = player
      :black (subvec (:pips board) 0 5)
      :white (subvec (:pips board) 17 23)))

(defn outer [board player]
  (condp = player
    :black (subvec (:pips board) 6 23)
    :white (subvec (:pips board) 0 16)))

(defn all-home? [board player]
  (let [player-outer (outer board player)]
    (none? #(belongs-to? % player) player-outer)))

(defn in-coll? [coll v]
  (if (empty? coll)
    false
    (or (= (first coll) v)
        (in-coll? (rest coll) v))))

(defn home? [pip board player]
  (in-coll? (home board player) pip))

(defn score? [board move]
  (= (:target move) ((:player board) (:scored board))))

(defn bearing-off [board move]
  (or
    (not (home? (:source move) board (:player board)))
    (not (score? board move))
    (all-home? board (:player board))))
