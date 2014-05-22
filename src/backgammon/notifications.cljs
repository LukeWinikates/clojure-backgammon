(ns backgammon.notifications)


(defn create [string] { :text string })

(defn has-prev? [notifications]
  (not
    (= 0 (current-idx notifications))))

(defn current-idx [notifications]
  (indexOf
    (:list notifications)
    (:current notifications)))

(defn has-next? [notifications]
  (not
    (= (- (count (:list notifications)) 1) (current-idx notifications))))

(defn add [string notifications]
  (let [new-notification (create string)]
        { :current new-notification
          :list (cons new-notification (:list notifications))}))

(defn init [] (add "foo!" {:list []}))

(defn indexOf [s item]
  (if (= (first s) item)
    0
    (+ 1 (indexOf (rest s) item))))

(defn page-notifications
  [board dir]
  (let [current (:current (:notifications board))
        l (:list (:notifications board))
        old-idx (indexOf l current)
        new-active-idx (dir old-idx 1)
        new-active (nth l new-active-idx)]
    (.log js/console l)
    (.log js/console old-idx)
    (.log js/console new-active-idx)
    (.log js/console new-active)
    (if new-active
      (assoc board
             :notifications
             (assoc (:notifications board) :current new-active))
      board)))
