(ns backgammon.server
  (:use compojure.core)
  (:use ring.middleware.resource)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [clj-jade.core :as jade]))

(jade/configure {
                 :template-dir "src/backgammon/templates/"
                 :pretty-print true })

(defroutes app-routes
  (GET "/" [] (jade/render "index.jade" {}))
  (route/resources "/"))

(def index (handler/site app-routes))

