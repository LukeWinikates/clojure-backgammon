(ns backgammon.board
  (:require [backgammon.dice :as dice]
            [backgammon.rules :as rules]
            [backgammon.bar :as bar]))

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

(defn make-score-pips []
  { :black (pip :black-scored :black 0)
    :white (pip :white-scored :white 0) })

(defn find-pip [board source die]
  (let [pips (:pips board)
        player (:player board)
        dir (if (= player :white) + -)
        is-bar? (= source (player (:bars board)))
        source-idx (if is-bar?
                    (if (= :black player)
                      24 -1)
                     (- (:index source) 1))
        target-idx (dir source-idx (:value die))
        bear-off? (or
                    (and (= player :black) (< target-idx 0))
                    (and (= player :white) (> target-idx 23)))]
    (.log js/console "target index" target-idx)
    (if bear-off?
      (player (:scored board))
      (nth pips target-idx))))


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

(defn apply-move-to-bar [bar move]
  (if (= (:source move) bar)
    (merge bar { :count (dec (:count bar))})
    bar))

(defn apply-move-to-bars [bars move]
  (let [black (:black bars)
        white (:white bars)]
  { :black (apply-move-to-bar black move)
    :white (apply-move-to-bar white move)}))

(defn apply-move [board move]
  (let [pips (:pips board)
        player (:player board)
        dice (:dice board)
        die (:die move)
        bars (:bars board)
        can-move? (and (dice/unused? die)
                       (can-move-from (:source move) player)
                       (can-move-to (:target move) player)
                       (rules/with-captured-checkers board move)
                       (rules/bearing-off board move))
        capture? (and can-move?
                      (is-opponent-blot? (:target move) player))
        new-bars (if capture?
                      (bar/capture bars (dice/swap-player player))
                      bars)]
    (.log js/console "args" board move)
    (.log js/console "can-move-from" (can-move-from (:source move) player))
    (.log js/console "can-move-to" (can-move-to (:target move) player))
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
         :bars (apply-move-to-bars new-bars move)
         :last-state board
         })
      board)))
