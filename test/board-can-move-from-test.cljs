(ns backgammon.board-can-move-from-test
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest with-test run-tests testing test-var)])
  (:require [cemerick.cljs.test :as t]
            [backgammon.board :as board]))

(deftest can-move-from-when-pip-is-owned-by-player-is-true
  (let [pip (board/pip 0 :white 5)]
    (is (board/can-move-from pip :white))))

(deftest can-move-from-when-pip-is-owned-by-other-player-is-false
  (let [pip (board/pip 0 :black 5)]
    (is (not (board/can-move-from pip :white)))))

(deftest can-move-from-when-pip-is-empty-is-false
  (is (not (board/can-move-from (board/pip 0) :white))))
