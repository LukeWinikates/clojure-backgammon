(ns backgammon.board
  (:require [backgammon.dice :as dice]
            [backgammon.gutter :as gutter]))

(defn pip
  ([idx] (pip idx nil 0))
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

(defn find-pip [board source-pip die]
  (let [pips (:pips board)
        dir (if (= (:player board) :white) + -)
        target-idx (dir (:index source-pip) (:value die))]
    (nth pips (- target-idx 1))))


(defn add-checker [pip color]
  (let [capture? (not (= (:owner pip) color))
        new-count (if capture? 1
                    (+ 1 (:count pip)))]
  (merge pip { :owner color :count new-count })))

(defn take-checker [pip color]
  (let [new-count (- (:count pip) 1)
        new-color (if (= 0 new-count)
                    nil color)]
  (merge pip { :owner new-color :count new-count })))

(defn can-move-to [pip player]
  (let [pip-owner (:owner pip)]
    (if (not (nil? pip-owner))
      (or (= pip-owner player) (< (:count pip) 2))
    true)))

(defn can-move-from [pip player]
  (let [pip-owner (:owner pip)]
    (= player pip-owner)))

(defn is-opponent-blot? [pip player]
  (and (= 1 (:count pip))
       (= (dice/swap-player player) (:owner pip))))

(defn undo [board]
  (:last-state board))

(defn build-move-from-source [board source]
  (let [active-die (dice/active (:dice board))]
    {:source source
     :die active-die
     :target (find-pip board source active-die)}))

(defn apply-move [board move]
  (let [pips (:pips board)
        player (:player board)
        dice (:dice board)
        die (:die move)
        gutters (:gutters board)
        can-move? (and (dice/unused? die)
                       (can-move-from (:source move) player)
                       (can-move-to (:target move) player))
        capture? (and can-move?
                      (is-opponent-blot? (:target move) player))
        new-gutters (if capture?
                      (gutter/capture gutters (dice/swap-player player))
                      gutters)]
    ;(.log js/console "args" board move)
    ;(.log js/console "can-move-from" (can-move-from (:source move) player))
    ;(.log js/console "can-move-to" (can-move-to (:target move) player))
    (if can-move?
      (do
        (.log js/console "we can move!")
        {
         :pips (replace
                 { (:target move) (add-checker (:target move) player)
                  (:source move) (take-checker (:source move) player) }
                 pips)
         :dice (dice/activate (dice/use-die die dice))
         :player player
         :gutters new-gutters
         :last-state board
         })
      board)))
