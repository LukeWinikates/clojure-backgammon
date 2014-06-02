(ns backgammon.ui
  (:require [om.core :as om :include-macros true]
            [backgammon.game :as game]
            [backgammon.components.turn-view :as turn-view]
            [backgammon.components.board-view :as board-view]))

(def app-state (atom (game/new-game)))

(defn boot []
  (om/root
    turn-view/build
    app-state
    {:target (. js/document (getElementById "sidebar"))})

  (om/root
    board-view/build
    app-state
    {:target (. js/document (getElementById "main-panel"))}))
