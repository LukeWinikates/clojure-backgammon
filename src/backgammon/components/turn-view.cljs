(ns backgammon.components.turn-view
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [backgammon.dice :as dice]
            [backgammon.die-view :as die-view]
            [cljs.core.async :refer [put! chan <!]]))

(defn undo-view [app undo-chan]
  (if-not (nil? (:last-state (:board app)))
    (dom/button
      #js {:onClick #(put! undo-chan 'ignore) :className "btn"}
      "Undo")))

(defn make-roll-button [roll]
  (dom/button
    #js {:onClick #(put! roll 'ignore) :className "btn" }
    "Roll"))

(defn build [app owner]
  (reify
    om/IInitState
    (init-state [_] { :roll (chan) :activate (chan) :undo (chan)})
    om/IWillMount
    (will-mount [_]
      (let [roll (om/get-state owner :roll)
            activate (om/get-state owner :activate)
            undo (om/get-state owner :undo)]
        (go (while true
              (let [msg (<! roll)]
                (om/transact! app :board dice/roll))))
        (go (while true
              (let [msg (<! undo)]
                (om/transact! app :board board/undo))))
        (go (while true
              (let [die (<! activate)]
                (.log js/console "activate!")
                (om/transact! app :board
                              (fn [board]
                                (assoc board :dice (dice/activate (:dice board) die)))))))))
    om/IRenderState
    (render-state [this {:keys [roll activate undo]}]
      (let [dice (:dice (:board app))]
        (dom/div nil
          (dom/h3 nil
            (str "Current player: " (name (:player (:board app)))))
          (undo-view app undo)
          (apply dom/h3 nil
                 (map #(die-view/build % activate) dice))
          (if (dice/all-used? dice)
            (make-roll-button roll)))))))

