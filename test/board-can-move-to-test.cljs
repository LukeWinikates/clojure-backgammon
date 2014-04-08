(ns backgammon.board-test
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest with-test run-tests testing test-var)])
  (:require [cemerick.cljs.test :as t]
            [backgammon.board :as board]))

(deftest can-move-to-when-pip-is-unowned-is-true
  (let [pip (board/pip 0)]
    (is (board/can-move-to pip :black))))

(deftest can-move-to-when-pip-is-owned-by-same-player-is-true
  (let [pip (board/pip 0 :black 3)]
    (is (board/can-move-to pip :black))))

(deftest can-move-to-when-pip-is-owned-by-other-player-but-is-a-blot-is-true
  (let [pip (board/pip 0 :white 1)]
    (is (board/can-move-to pip :black))))

(deftest can-move-to-when-pip-is-owned-by-other-player-is-false
  (let [pip (board/pip 0 :white 2)]
    (is (not (board/can-move-to pip :black)))))
