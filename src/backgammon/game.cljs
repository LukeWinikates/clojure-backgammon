(ns backgammon.game
  (:require [backgammon.notifications :as notf]
            [backgammon.board :as board]
            [backgammon.dice :as dice]))

(defn new-game []
  (let [pick (dice/pick-first-player)
        player (:player pick)
        notifications (notf/add (str (name player) " moves first!")(notf/init))]
  {:board (merge
            { :notifications notifications }
            (board/nack-board)
            pick
            { :bars { :white { :count 0 :owner :white } :black { :count 0 :owner :black } } } )}))
