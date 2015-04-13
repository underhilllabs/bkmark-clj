(ns bkmarker.core
  (:require [compojure.core :refer :all]
            [ring.middleware.params :refer :all]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.util.response :as resp]
            [compojure.route :refer :all]
            [bkmarker.models.model :refer :all]
            [bkmarker.views.bookmarks :as v]
            [bkmarker.db.dbconn :refer [db-spec]]
            [yesql.core :refer [defqueries]]
            [korma.core :refer :all]
            [noir.util.route :refer [restricted]]
            [noir.util.middleware :refer [app-handler]]
            [noir.util.crypt :as crypt]
            [noir.session :as session]
            [hiccup.core :as hiccup]
            [hiccup.page :as h]
            [hiccup.element :refer [javascript-tag]]
            [clojure.java.jdbc :as j]
            [clojure.string :as s]
            [org.httpkit.server :refer [run-server]])
  (:gen-class :main true))

;; load the yesql queries
(defqueries "bkmarker/models/bookmark.sql")

;; timeout sessions after 30 days
(def session-defaults
  {:timeout (* 60 30 24)
   :timeout-response (resp/redirect "/")})

(defn pr-bkmarks-tag
  "Let's print all the tagged bookmarks!"
  [params lim off]
  (let [page (get params "page" "1")
        my-tag (get params :tagname)
        page-num (Integer/parseInt page)
        offset (* page-num lim)]
    (v/main-layout 
     (str "Bookmarks tagged with: " (s/capitalize my-tag))
     (apply str 
            (map #(v/view-bookmark %)  
                 (bkmarks-tag-query my-tag lim offset)))
     (v/view-pagination-simple (str "/tag/name/" my-tag) page-num))))

(defn pr-bkmarks-user
  "Print all the users bookmarks!"
  [params lim off]
  (let [my-user (get params :username)
        page (get params "page" "1")
        page-num (Integer/parseInt page)
        offset (* (dec page-num) lim)]
    (v/main-layout 
     (str (s/capitalize my-user) 
          "'s Bookmarks")
     (apply str 
            (map #(v/view-bookmark %)  
                 (bkmarks-user-query my-user lim offset)))
     (v/view-pagination-simple (str "/user/" my-user) page-num))))

(defn pr-user-bkmark-count
  "Show all user bookmark count!"
  []
  (v/main-layout "Bkmarker Users!"
   (apply str
          (map #(v/view-user-bookmark-count %)  
               (users-bookmark-count-query)))))

(defn pr-bkmark-query
  "Show results of bookmark query!"
  [title query kw page-num]
  (v/main-layout 
   title
   (apply str
          (map #(v/view-bookmark %)  
               query))
   (v/view-pagination-simple (str "/search/?keywords=" kw) (Integer/parseInt page-num))))

(defn pr-search-page
  "Show search results"
  [params lim off]
  (let [kw (get params "keywords")
        page (get params "page" "1")
        offset (* (dec (Integer/parseInt page)) lim)]
    (pr-bkmark-query
     (str "Search results for " kw)
     (bkmarks-search-query kw lim offset)
     kw
     page)))

(defn pr-tags-count
  "Print all user bookmark count!"
  []
  (v/main-layout "Bkmarker Tags!"
   (apply str
          (map #(v/view-tag-count %)  
               (tags-count-query)))))

(defn pr-bkmarks
  "Show all the bookmarks!"
  [params lim off]
  (let [page (get params "page" "1")
        page-num (Integer/parseInt page)
        offset (* lim (dec page-num))]
    (v/main-layout 
     "Welcome to Bkmarker!"
     (apply str
            (map #(v/view-bookmark %)  
                 (bkmarks-query lim offset)))
     (v/view-pagination-simple "/" page-num))))

(defn pr-my-bkmarks
  "Show all of my bookmarks!"
  [params lim off]
  (let [page (get params "page" "1")
        page-num (Integer/parseInt page)
        offset (* lim (dec page-num))]
    (v/main-layout 
     "Your bookmarks"
     (apply str
            (map #(v/view-bookmark %)  
                 (bkmarks-query lim offset)))
     (v/view-pagination-simple "/" page-num))))

(def my-limit 20)

(defn auth-user
  [params]
  (if-let [user (first (find-user-email (get params "username")))]
    (if (crypt/compare (get params "password") (get user :password_digest))
      (do
        (session/put! :user-id (get user :id))
        (session/put! :user-name (get user :username))
        (resp/redirect "/profile/"))
      (resp/redirect "/login?q=incorrect_login"))
    (resp/redirect "/login?q=incorrect_email")))

(defn register-user
  [params]
  (let [username (get params "username")
        password (get params "userpass")
        email (get params "email")
        password-digest (crypt/encrypt password)
        user-id (create-user username email password-digest)]
    (do
      (session/put! :user-id user-id)
      (session/put! :username username)
      (resp/redirect "/profile"))))

;; (if-let [user_id (session/get :user_id)] ...
(defn pr-my-profile
  "Show logged in user's profile"
  []
  (if-let [user-id (session/get :user-id)]
    (if-let [user (first (find-user-by-id user-id))]
      (v/view-profile user)
      (resp/redirect "/login?q=user_not_found"))
    (resp/redirect "/login?q=not_logged_in")))

(defn pr-user-profile
  "Show a user's profile"
  [params]
  (if-let [user-id (params :id)]
    (if-let [user (first (find-user-by-id user-id))]
      (v/view-profile user)
      (resp/redirect (str "/?q=user_not_found&id=" user-id)))
  (resp/redirect (str "/?q=user_not_found&id=" params))))

(defn pr-bookmark-form
  []
  (if-let [id (session/get :user-id)]
    (v/view-bookmark-form id)
    (resp/redirect "/login?q=requires_auth")))

(defn pr-bookmarklet-form
  [params]
  (if-let [id (session/get :user-id)]
    (v/view-bookmarklet-form id params)
    (resp/redirect "/login?q=requires_auth")))

(defn pr-create-bookmark
  [params]
  (if-let [id (session/get :user-id)]
    (do
      (let [bk-id (create-bookmark<! db-spec (params "title") (params "address") (params "description") id)]
        (when-let [tags (params "tags")]
          (for [tag (clojure.string/split tags #",\s+")]
            (create-tag! db-spec tag bk-id id))))
      (if (params "bookmarklet") 
        (hiccup/html 
         (javascript-tag "window.close();"))
        (resp/redirect "/profile/?q=bookmark_created")))
    (resp/redirect "/login?q=requires_auth")))

(defroutes bkmark-routes 
  (GET "/" {params :params} (pr-bkmarks params my-limit 0))
  (GET "/bookmarks/user/:username" {params :params}
       (pr-bkmarks-user params my-limit 0))  
  (GET "/bookmarks/" {params :params} 
       (restricted (pr-my-bkmarks params my-limit 0)))
  (GET "/bookmarklet" {params :params} 
       (pr-bookmarklet-form params))
  (GET "/bookmarks/new" [] 
       (pr-bookmark-form))
  (POST "/bookmarks/new" {params :params} 
       (pr-create-bookmark params))
  (GET "/user/:user" {params :params}
       (pr-bkmarks-user params my-limit 0))
  (GET "/user/" [] (pr-user-bkmark-count))
  (GET "/tag/name/:tagname" {params :params} (pr-bkmarks-tag params my-limit 0))
  (GET "/tags/" [] (pr-tags-count))
  (GET "/profile/:id" {params :params}  (pr-user-profile params))
  (GET "/profile/" [] (pr-my-profile))
  (GET "/search/"
       {params :params}
       (pr-search-page params my-limit 0))
  (GET "/login" []
      (v/view-login-page))
  (GET "/register" []
       (v/view-register-page))
  (POST "/register" {params :params}
        (register-user params))
  (POST "/login" {params :params}
        (auth-user params))        
  (resources "/")
  (not-found "Page not found."))

(defn -main []
  (run-server (session/wrap-noir-session (wrap-params bkmark-routes)) {:port 5010}))

