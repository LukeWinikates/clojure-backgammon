(ns backgammon.rules-with-captured-checkers-test
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest)])
  (:require [cemerick.cljs.test :as t]
            [backgammon.rules :as rules]
            [backgammon.board :as board]
            [backgammon.dice :as dice]
            [backgammon.bar :as bar]))

(def new-board
  {
   :player :black
   :pips (:pips (board/nack-board))
   :dice (dice/from-values [5 3])
   :bars (bar/new-bars)
   })

(deftest with-captured-checkers-when-no-checker-is-captured-for-current-player-is-true
  (let [board new-board
        move { :source (nth (:pips board) 12)
            :die (first (:dice board)) }]
    (is (rules/with-captured-checkers board move))))


(deftest with-captured-checkers-when-this-player-has-a-captured-checker-but-is-trying-to-move-from-a-regular-pip-is-false
  (let [board (merge new-board { :bars { :black { :count 1 } :white { :count 0 }}})
        move { :source (nth (:pips board) 12)
            :die (first (:dice board)) }]
    (is (not (rules/with-captured-checkers board move)))))

(deftest with-captured-checkers-when-this-player-is-captured-and-is-moving-captured-checker-is-true
  (let [board (merge new-board { :bars { :black { :count 1 } :white { :count 0 }}})
        move { :source (:black (:bars board))
            :die (first (:dice board)) }]
    (is (= true (rules/with-captured-checkers board move)))))
