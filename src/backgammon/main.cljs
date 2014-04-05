(ns backgammon.ui
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]))
(defn pip
  ([] { :owner nil :count 0 })
  ([owner size] { :owner owner :count size } ))

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

(def app-state (atom {:board (nack-board) }))



(defn pip-view [pip owner]
  (reify
    om/IRenderState
    (render-state [this owner]
      (let [count (:count pip)]
      (dom/li #js { :onClick (fn [e] (.log js/console "clicked!") )}
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


