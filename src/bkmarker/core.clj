(ns bkmarker.core
  (:require [compojure.core :refer :all]
            [compojure.route :refer :all]
            [bkmarker.models.model :refer :all]
            [bkmarker.views.bookmarks :as v]
            [korma.core :refer :all]
            [hiccup.core :as hiccup]
            [clojure.java.jdbc :as j]
            [org.httpkit.server :refer [run-server]])
  (:gen-class :main true))

(defn pr-bkmarks-user
  "Let's print all the users bookmarks!"
  [in-user lim off]
  (v/main-layout 
   (apply str 
          (map #(v/view-bookmark %)  
               (select bookmarks 
                       (with users)
                       (with tags)
                       (where {:users.username in-user})
                       (order :updated_at :DESC)
                       (limit lim)
                       (offset off))))))

(defn pr-bkmarks
  "Let's print all the bookmarks!"
  [lim off]
  (v/main-layout
   (apply str
          (map #(v/view-bookmark %)  
               (select bookmarks 
                       (with users)
                       (with tags)
                       (order :updated_at :DESC)
                       (limit lim)
                       (offset off))))))

(defroutes bkmark-routes 
  (GET "/" [] (pr-bkmarks 20 0))
  (GET "/user/:user" [user] (pr-bkmarks-user user 20 0))
  (resources "/")
  (not-found "Page not found"))

(defn -main []
  (run-server bkmark-routes {:port 5010}))
