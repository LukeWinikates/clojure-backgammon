(ns backgammon.ui
 (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defn board []
  {:pips (vec (repeatedly 24 pip))})

(defn nack-board []
  {:pips [(pip :white 2)
          (pip :white 2)
          (pip)
          (pip)
          (pip)
          (pip :black 4)
          (pip)
          (pip :black 3)
          (pip)
          (pip)
          (pip)
          (pip :white 4)
          (pip :black 4)
          (pip)
          (pip)
          (pip)
          (pip :white 3)
          (pip)
          (pip :white 4)
          (pip)
          (pip)
          (pip)
          (pip :black 2)
          (pip :black 2)]})

(defn pip
  ([] { :owner nil :count 0 })
  ([owner size] { :owner owner :count size } ))

(def app-state (atom {:board (nack-board) }))

(defn render-pip [pip]
  (let [count (:count pip)]
  (dom/li nil
    (if (= count 0)
      "empty"
      (str (name (:owner pip)) ": " count)))))

(defn board-view [app owner]
  (reify
    om/IRender
    (render [this]
      (dom/div #js{:className "board"}
        (dom/h2 nil "Board")
        (dom/div nil "Black")
        (apply dom/ul nil
          (map render-pip (:pips (:board app))))
        (dom/div nil "White")))))

(om/root
  board-view
  app-state
  {:target (. js/document (getElementById "root"))})


