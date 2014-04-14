(ns backgammon.board)

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

(defn find-pip [board source-pip die]
  (let [pips (:pips board)
        dir (if (= (:player board) :white) + -)
        target-idx (dir (:index source-pip) (:value die))]
    (nth pips (- target-idx 1))))

(defn use-die [die dice]
  dice)

(defn add-checker [pip color]
  (let [new-count (+ 1 (:count pip))]
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

(defn apply-move [board source-pip die]
  (let [pips (:pips board)
        player (:player board)
        dice (:dice board)
        target-pip (find-pip board source-pip die)
        can-move? (and (can-move-from source-pip player)
                       (can-move-to target-pip player))]
    (if can-move?
      (do
        (.log js/console "we can move!")
        {
         :pips (replace
                 { target-pip (add-checker target-pip player)
                  source-pip (take-checker source-pip player) }
                 pips)
         :dice (use-die die dice)
         :player player
         })
      board)))
