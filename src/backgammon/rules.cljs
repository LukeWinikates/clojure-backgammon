(ns backgammon.rules)

(defn with-captured-checkers [board move]
  (let [player (:player board)
        player-gutter (player (:gutters board))
        is-captured? (> (:count player-gutter) 0)]
    (.log js/console "player-gutter" player-gutter)
    (or (not is-captured?)
        (= (:source move) player-gutter))))
