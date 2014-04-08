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
        dir (if (= (:player board) :black) + -)
        target-idx (dir (:index source-pip) (:value die))]
    (nth pips (- target-idx 1))))

(defn apply-move [board target-pip]
  (let [new-pip (merge target-pip { :count (+ 1 (:count target-pip))})]
    (.log js/console "target:" (find-pip board target-pip (first (:dice board))))
    (merge board
      { :pips (replace { target-pip new-pip } (:pips board))})))

(defn can-move-to [pip player]
  (let [pip-owner (:owner pip)]
    (if (not (nil? pip-owner))
      (or (= pip-owner player) (< (:count pip) 2))
    true)))
