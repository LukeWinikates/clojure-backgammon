(ns backgammon.ui
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string]
            [backgammon.dice :as dice]
            [backgammon.notifications :as notf]
            [backgammon.board :as board]
            [backgammon.bar :as bar]
            [cljs.core.async :refer [put! chan <!]]))

(defn new-game []
  (let [pick (dice/pick-first-player)
        player (:player pick)
        notifications (notf/add (str (name player) " moves first!")(notf/init))]
  {:board (merge
            { :notifications notifications }
            (board/nack-board)
            pick
            { :bars { :white { :count 0 :owner :white } :black { :count 0 :owner :black } } } )}))


(def app-state (atom (new-game)))

(defn send-move [move pip]
  (.log js/console "clicked!" (:index pip) pip)
  (put! move pip))

(defn notifications-view [notifications]
  (dom/div
    #js{:className "notifications bordered"}
    (dom/div
      #js{:className "notification"}
      (:text (:active notifications)))))

(defn die-classes [die]
  (str "die "
       (if (:active die) "active-die" "inactive-die")
       (if (:used die) " used-die")))

(defn make-die-view [die activate]
  (dom/div #js { :onClick (fn [e] (put! activate die))
                :className (die-classes die) }
           (:value die)))

(defn make-roll-button [roll]
  (dom/button
    #js {:onClick #(put! roll 'ignore) :className "btn" }
    "Roll"))

(defn undo-view [app undo-chan]
  (if-not (nil? (:last-state (:board app)))
    (dom/button
      #js {:onClick #(put! undo-chan 'ignore) :className "btn"}
      "Undo")))

(defn turn-view [app owner]
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
                 (map #(make-die-view % activate) dice))
          (if (dice/all-used? dice)
            (make-roll-button roll)))))))

(defn pip-classes [pip]
  (clojure.string.join " "
    ["pip"
     (if (even? (:index pip))
        "red"
        "black")]))

(defn make-pip-view [pip move-chan]
  (let [checker-count (:count pip)]
    (dom/li #js { :onDoubleClick (fn [e] (send-move move-chan @pip))
                 :className (pip-classes pip) }
            (if (> checker-count 0)
              (str (name (:owner pip)) ": " checker-count)))))

(defn bar-view [bar move-chan]
  (dom/li #js {:className "bar pip" :onDoubleClick (fn [e] (send-move move-chan @bar)) }
          (str (name (:owner bar)) ": " (:count bar))))


(defn black-home [board]
  (let [pips (:pips board)]
    (reverse (subvec (:pips board) 0 (/ (count pips) 4)))))

(defn black-outer [board]
  (let [pips (:pips board)
        quad (/ (count pips) 4)
        start quad
        end (* 2 quad)]
    (reverse (subvec (:pips board) start end))))

(defn white-outer [board]
  (let [pips (:pips board)
        quad (/ (count pips) 4)
        start (* 2 quad)
        end (* 3 quad)]
    (subvec (:pips board) start end)))

(defn white-home [board]
  (let [pips (:pips board)
        quad (/ (count pips) 4)
        start (* 3 quad)
        end (count pips)]
    (subvec (:pips board) start end)))

(defn board-view [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:move (chan)})
    om/IWillMount
    (will-mount [_]
      (let [move (om/get-state owner :move)]
        (go (while true
          (let [pip (<! move)]
            (om/transact! app :board
              (fn [board]
                (board/apply-move
                  board
                  (board/build-move-from-source board pip)))))))))
    om/IRenderState
    (render-state [this {:keys [move]}]
      (let [pips (:pips (:board app))
            black-bar (:black (:bars (:board app)))
            white-bar (:white (:bars (:board app)))
            top-pips (reverse (subvec pips 0 (/(count pips) 2)))
            bottom-pips (subvec pips (/ (count pips) 2) (count pips))]
        (dom/div
          nil
          (notifications-view (:notifications (:board app)))
          (dom/div
            #js{:className "board"}
            (let [home (black-home (:board app))
                  outer (black-outer (:board app))]
              (dom/div
                nil
                (dom/div
                  #js {:className "pip-row"}
                  (apply
                    dom/ul
                    #js{:className "top-pips pips" }
                    (concat
                      (map #(make-pip-view % move) outer)
                      [(bar-view white-bar move)]
                      (map #(make-pip-view % move) home))))))
            (let [home (white-home (:board app))
                  outer (white-outer (:board app))]
              (dom/div
                nil
                (dom/div
                  #js {:className "pip-row"}
                  (apply
                    dom/ul
                    #js{:className "top-pips pips" }
                    (concat
                      (map #(make-pip-view % move) outer)
                      [(bar-view black-bar move)]
                      (map #(make-pip-view % move) home))))))))))))

(defn boot []
  (om/root
    turn-view
    app-state
    {:target (. js/document (getElementById "sidebar"))})

  (om/root
    board-view
    app-state
    {:target (. js/document (getElementById "main-panel"))}))
