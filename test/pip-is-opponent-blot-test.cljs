(ns backgammon.board-is-opponent-blot-test
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest)])
  (:require [cemerick.cljs.test :as t]
            [backgammon.rules :as rules]
            [backgammon.board :as board]
            [backgammon.gutter :as gutter]))

(deftest is-opponent-blot-test-when-pip-is-empty-returns-false
  (is (not (board/is-opponent-blot? (board/pip 0) :black))))

(deftest is-opponent-blot-when-pip-belongs-to-this-player-returns-false
  (is (not (board/is-opponent-blot? (board/pip 0 :black 1) :black))))

(deftest is-opponent-blot-when-pip-belongs-to-other-player-and-has-count-of-one-returns-true
  (is (board/is-opponent-blot? (board/pip 0 :black 1) :white)))
