(ns backgammon.rules-bearing-off-test
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest)])
  (:require [cemerick.cljs.test :as t]
            [backgammon.rules :as rules]
            [backgammon.board :as board]
            [backgammon.dice :as dice]
            [backgammon.bar :as bar]))

(def new-board
  { :player :black
    :pips (:pips (board/nack-board))
    :dice (dice/from-values [5 3])
    :bars (bar/new-bars)
    :scored (board/make-score-pips) })

(def ^:private black-ready-to-bear-off
  (merge
    new-board
    {:pips [(board/pip 1 :black 2)
            (board/pip 2 :black 2)
            (board/pip 3)
            (board/pip 4)
            (board/pip 5 :black 1)
            (board/pip 6)
            (board/pip 7)
            (board/pip 8)
            (board/pip 9)
            (board/pip 10)
            (board/pip 11)
            (board/pip 12)
            (board/pip 13)
            (board/pip 14)
            (board/pip 15)
            (board/pip 16)
            (board/pip 17)
            (board/pip 18)
            (board/pip 19)
            (board/pip 20)
            (board/pip 21)
            (board/pip 22)
            (board/pip 23)
            (board/pip 24)]}))

(def ^:private black-can-not-bear-off
  (merge
    new-board
    {:pips [(board/pip 1 :black 2)
            (board/pip 2 :black 2)
            (board/pip 3)
            (board/pip 4)
            (board/pip 5)
            (board/pip 6 :black 1)
            (board/pip 7)
            (board/pip 8)
            (board/pip 9)
            (board/pip 10)
            (board/pip 11)
            (board/pip 12)
            (board/pip 13)
            (board/pip 14)
            (board/pip 15 :black 4)
            (board/pip 16)
            (board/pip 17)
            (board/pip 18)
            (board/pip 19)
            (board/pip 20)
            (board/pip 21)
            (board/pip 22)
            (board/pip 23)
            (board/pip 24)]}))

(deftest bearing-off-when-move-is-not-from-a-home-pip-is-true
  (let [board new-board
        source (nth (:pips board) 14)
        move  (board/build-move-from-source board source)]
    (is (rules/bearing-off board move))))


(deftest bearing-off-when-move-is-home-pip-all-checkers-are-in-home-is-true
  (let [board black-ready-to-bear-off
        source (nth (:pips board) 4)
        move  (board/build-move-from-source board source)]
    (is (rules/bearing-off board move))))

(deftest bearing-off-when-move-is-home-but-would-not-go-off-board-is-true
  (let [board black-can-not-bear-off
        source (nth (:pips board) 5)
        move  (board/build-move-from-source board source)]
    (is (rules/bearing-off board move))))

(deftest bearing-off-when-any-pieces-are-not-in-home-is-false
  (let [board black-can-not-bear-off
        source (nth (:pips board) 1)
        move  (board/build-move-from-source board source)]
    (is (not (rules/bearing-off board move)))))
