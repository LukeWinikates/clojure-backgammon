(ns backgammon.ui
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]))
(defn pip
  ([idx] { :index idx :owner nil :count 0 })
  ([idx owner size] { :index idx :owner owner :count size } ))

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

(defn apply-move [board target-pip]
  { :pips (replace { target-pip
    (merge target-pip
      { :count (+ 1 (:count target-pip))})} (:pips board))})

(defn send-move [move pip]
  (.log js/console "clicked!" (:index pip))
  (put! move pip))

(defn pip-view [pip owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [move]}]
      (let [count (:count pip)]
      (dom/li #js { :onClick (fn [e] (send-move move @pip))}
        (if (= count 0)
          "empty"
          (str (name (:owner pip)) ": " count)))))))

(defn board-view [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:move (chan)})
    om/IWillMount
    (will-mount [_]
      (let [move (om/get-state owner :move)]
        (go (loop []
          (let [pip (<! move)]
            (om/transact! app :board
              (fn [board] (apply-move board pip)))
            (recur))))))
    om/IRenderState
    (render-state [this {:keys [move]}]
      (dom/div #js{:className "board"}
        (dom/h2 nil "Board")
        (dom/div nil "Black")
        (apply dom/ul nil
          (om/build-all pip-view (:pips (:board app))
            {:init-state {:move move }}))
        (dom/div nil "White")))))

(om/root
  board-view
  app-state
  {:target (. js/document (getElementById "root"))})
