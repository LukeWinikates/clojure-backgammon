(ns backgammon.notifications)

(defn init []
  { :active nil
    :list [] })

(defn create
  [string]
  { :text string })

(defn add [string notifications]
  (let [new-notification (create string)]
    { :active new-notification
     :list (cons new-notification (:list notifications))}))

