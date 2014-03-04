(ns app.main
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]))

(defroutes app-routes
  (GET "/pages/:page_id" [page_id]
       (str "You chose page: " page_id))
  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  (handler/site app-routes))
