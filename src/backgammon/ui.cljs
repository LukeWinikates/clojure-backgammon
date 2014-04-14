(ns backgammon.ui
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [backgammon.dice :as dice]
            [backgammon.board :as board]
            [cljs.core.async :refer [put! chan <!]]))

(def app-state (atom
   {:board (merge (board/nack-board) (dice/pick-first-player))}))

(defn send-move [move pip]
  (.log js/console "clicked!" (:index pip))
  (put! move pip))

(defn die-view [die owner]
  (reify
    om/IInitState
    (init-state [_]
      (.log js/console "die" die)
      { :activate (chan) })
    om/IWillMount
    (will-mount [_]
      (let [activate (om/get-state owner :activate)]
        (go (loop []
              (let [die (<! activate)]
                (om/transact! app :board
                              (fn [board] (merge board { :dice (dice/activate (:dice board) die) })))
                (recur))))))
    om/IRenderState
    (render-state [this {:keys [activate]}]
      (dom/div #js { :onClick (fn [e] (put! activate die))
                      :className (die-classes die) }
                (:value die)))))

(defn die-classes
  [die]
  (str "die " (if (:active die) "active-die" "inactive-die" (if (:used die) "used-die"))))

(defn turn-view [app owner]
  (reify
    om/IInitState
    (init-state [_]
      { :roll (chan) })
    om/IWillMount
    (will-mount [_]
      (let [roll (om/get-state owner :roll)]
        (go (loop []
              (let [msg (<! roll)]
                (om/transact! app :board
                              dice/roll)
              (recur))))))
    om/IRenderState
    (render-state [this {:keys [roll]}]
      (let [dice (:dice (:board app))]
        (dom/div
          nil
          (dom/h3
            nil
            (str "Current player: " (name (:player (:board app)))))
          (apply dom/h3 nil
                 (om/build-all die-view dice))
          (if (dice/all-used? dice)
            (dom/span
             # js { :onClick (fn [e]
                               (.log js/console "rolling!")
                               (put! roll "ignore"))}
              "Roll")))))))

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
              (fn [board] (board/apply-move board pip (dice/active (:dice board)))))
            (recur))))))
    om/IRenderState
    (render-state [this {:keys [move]}]
      (dom/div #js{:className "board"}
        (dom/div nil "Black")
        (apply dom/ul nil
          (om/build-all pip-view (:pips (:board app))
            {:init-state {:move move }}))
        (dom/div nil "White")))))

(om/root
  turn-view
  app-state
  {:target (. js/document (getElementById "sidebar"))})

(om/root
  board-view
  app-state
  {:target (. js/document (getElementById "main-panel"))})
