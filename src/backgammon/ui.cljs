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

(defn make-die-view [die activate]
  (dom/div #js { :onClick (fn [e] (put! activate die))
                :className (die-classes die) }
           (:value die)))

(defn die-classes
  [die]
  (str "die "
       (if (:active die) "active-die" "inactive-die")
       (if (:used die) " used-die")))

(defn turn-view [app owner]
  (reify
    om/IInitState
    (init-state [_] { :roll (chan) :activate (chan)})
    om/IWillMount
    (will-mount [_]
      (let [roll (om/get-state owner :roll)
            activate (om/get-state owner :activate)]
        (go (loop []
              (let [msg (<! roll)]
                (om/transact! app :board
                              dice/roll)
              (recur))))
            (go (while true
                  (let [die (<! activate)]
                    (.log js/console "activate!")
                    (om/transact! app :board
                       (fn [board]
                         (assoc board :dice (dice/activate (:dice board) die)))))))))
    om/IRenderState
    (render-state [this {:keys [roll activate]}]
      (let [dice (:dice (:board app))]
        (dom/div nil
          (dom/h3 nil
            (str "Current player: " (name (:player (:board app)))))
          (apply dom/h3 nil
                 (map #(make-die-view % activate) dice))
          (if (dice/all-used? dice)
            (make-roll-button roll)))))))

(defn make-roll-button [roll]
  (dom/button
    #js {:onClick #(put! roll 'ignore) :className "btn" }
    "Roll"))

(defn make-pip-view [pip pips move-chan]
  (let [checker-count (:count pip)]
    (dom/li #js { :onClick (fn [e] (send-move move-chan @pip))
                 :className "pip" }
            (if (= checker-count 0)
              "empty"
              (str (name (:owner pip)) ": " checker-count)))))

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
      (let [pips (:pips (:board app))]
        (dom/div #js{:className "board"}
          (dom/div nil "Black")
            (apply dom/ul nil
               (map #(make-pip-view % pips move) pips ))
          (dom/div nil "White"))))))

(om/root
  turn-view
  app-state
  {:target (. js/document (getElementById "sidebar"))})

(om/root
  board-view
  app-state
  {:target (. js/document (getElementById "main-panel"))})
