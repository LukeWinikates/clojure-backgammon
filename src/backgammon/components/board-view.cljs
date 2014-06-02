(ns backgammon.components.board-view
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [clojure.string]
            [backgammon.dice :as dice]
            [backgammon.notifications :as notf]
            [backgammon.notifications-view :as notfv]
            [backgammon.board :as board]
            [backgammon.bar :as bar]
            [cljs.core.async :refer [put! chan <!]]))

(defn pip-classes [pip]
  (clojure.string.join " "
    ["pip"
     (if (even? (:index pip))
        "red"
        "black")]))

(defn make-pip-view [pip move-chan]
  (let [checker-count (:count pip)]
    (dom/li #js { :onDoubleClick #(put! move-chan @pip)
                 :className (pip-classes pip) }
            (if (> checker-count 0)
              (str (name (:owner pip)) ": " checker-count)))))

(defn bar-view [bar move-chan]
  (dom/li #js {:className "bar pip" :onDoubleClick #(put! move-chan @bar) }
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

(defn build [app owner]
  (reify
    om/IInitState
    (init-state [_]
      {:move (chan) :spin-notification (chan)})
    om/IWillMount
    (will-mount [_]
      (let [move (om/get-state owner :move)
            spin (om/get-state owner :spin-notification)]
        (go (while true
              (let [dir (<! spin)]
                (om/transact! app :board #(notf/page-notifications % dir)))))
        (go (while true
          (let [pip (<! move)]
            (om/transact! app :board
              (fn [board]
                (board/apply-move
                  board
                  (board/build-move-from-source board pip)))))))))
    om/IRenderState
    (render-state [this {:keys [move spin-notification]}]
      (let [pips (:pips (:board app))
            black-bar (:black (:bars (:board app)))
            white-bar (:white (:bars (:board app)))
            top-pips (reverse (subvec pips 0 (/(count pips) 2)))
            bottom-pips (subvec pips (/ (count pips) 2) (count pips))]
        (dom/div
          nil
          (notfv/notifications-view (:notifications (:board app)) spin-notification)
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
