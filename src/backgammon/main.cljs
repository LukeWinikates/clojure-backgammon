(ns backgammon.ui
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]))
(defn pip
  ([idx] { :index idx :owner nil :count 0 })
  ([idx owner size] { :index idx :owner owner :count size } ))

;(defn board []
  ;{:pips (vec (repeatedly 24 pip))})

(defn nack-board []
  {:pips [(pip 1 :white 2)
          (pip 2 :white 2)
          (pip 3)
          (pip 4)
          (pip 5)
          (pip 6 :black 4)
          (pip 7)
          (pip 8 :black 3)
          (pip 9)
          (pip 10)
          (pip 11)
          (pip 12 :white 4)
          (pip 13 :black 4)
          (pip 14)
          (pip 15)
          (pip 16)
          (pip 17 :white 3)
          (pip 18)
          (pip 19 :white 4)
          (pip 20)
          (pip 21)
          (pip 22)
          (pip 23 :black 2)
          (pip 24 :black 2)]})

(def app-state (atom {:board (nack-board) }))

(defn send-move [pip]
  (.log js/console "clicked!" (:index @pip)))

(defn pip-view [pip owner]
  (reify
    om/IRenderState
    (render-state [this owner]
      (let [count (:count pip)]
      (dom/li #js { :onClick (fn [e] (send-move pip) )}
        (if (= count 0)
          "empty"
          (str (name (:owner pip)) ": " count)))))))

(defn board-view [app owner]
  (reify
    om/IInitState
    (init-state [_]
      { :move (chan)})
    om/IWillMount
    (will-mount [_]
      ())
    om/IRenderState
    (render-state [this owner]
      (dom/div #js{:className "board"}
        (dom/h2 nil "Board")
        (dom/div nil "Black")
        (apply dom/ul nil
          (om/build-all pip-view (:pips (:board app)) {}))
        (dom/div nil "White")))))

(om/root
  board-view
  app-state
  {:target (. js/document (getElementById "root"))})


