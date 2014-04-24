(ns backgammon.rules)

(defn with-captured-checkers [board move]
  (let [player (:player board)
        player-bar (player (:bars board))
        is-captured? (> (:count player-bar) 0)]
    (or (not is-captured?)
        (= (:source move) player-bar))))
