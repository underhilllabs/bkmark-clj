(ns bkmarker.core
  (:require [compojure.core :refer :all]
            [ring.middleware.params :refer :all]
            [compojure.route :refer :all]
            [bkmarker.models.model :refer :all]
            [bkmarker.views.bookmarks :as v]
            [korma.core :refer :all]
            [hiccup.core :as hiccup]
            [clojure.java.jdbc :as j]
            [clojure.string :as s]
            [org.httpkit.server :refer [run-server]])
  (:gen-class :main true))

(defn pr-bkmarks-tag
  "Let's print all the tagged bookmarks!"
  [my-tag lim off]
  (v/main-layout 
   (str "Bookmarks tagged with: " (s/capitalize my-tag))
   (apply str 
          (map #(v/view-bookmark %)  
               (bkmarks-tag-query my-tag lim off)))))

(defn pr-bkmarks-user
  "Let's print all the users bookmarks!"
  [my-user lim off]
  (v/main-layout 
   (str (s/capitalize my-user) 
        "'s Bookmarks")
   (apply str 
          (map #(v/view-bookmark %)  
               (bkmarks-user-query my-user lim off)))))

(defn pr-user-bkmark-count
  "Show all user bookmark count!"
  []
  (v/main-layout "Bkmarker Users!"
   (apply str
          (map #(v/view-user-bookmark-count %)  
               (users-bookmark-count-query)))))

(defn pr-bkmark-query
  "Show results of bookmark query!"
  [title query]
  (v/main-layout title
   (apply str
          (map #(v/view-bookmark %)  
               query))))

(defn pr-search-page
  "Show search results"
  [params lim off]
  (let [kw (get params "keywords")]
    (pr-bkmark-query
     (str "Search results for " kw)
     (bkmarks-search-query kw lim off))))

(defn pr-tags-count
  "Print all user bookmark count!"
  []
  (v/main-layout "Bkmarker Tags!"
   (apply str
          (map #(v/view-tag-count %)  
               (tags-count-query)))))

(defn pr-bkmarks
  "Print all the bookmarks!"
  [params lim off]
  (let [page (get params "page" "1")
        offset (dec (Integer/parseInt page))]
    (v/main-layout 
     "Welcome to Bkmarker!"
     (apply str
            (map #(v/view-bookmark %)  
                 (bkmarks-query lim offset))))))

(def my-limit 20)

(defroutes bkmark-routes 
  (GET "/" {params :params} (pr-bkmarks params my-limit 0))
  (GET "/user/:user" [user] (pr-bkmarks-user user my-limit 0))
  (GET "/user/" [] (pr-user-bkmark-count))
  (GET "/tag/name/:tagname" [tagname] (pr-bkmarks-tag tagname my-limit 0))
  (GET "/tags/" [] (pr-tags-count))
  (GET "/search/" 
       {params :params} 
       (pr-search-page params my-limit 0))
  (resources "/")
  (not-found "Page not found"))

(defn -main []
  (run-server (wrap-params bkmark-routes) {:port 5010}))
