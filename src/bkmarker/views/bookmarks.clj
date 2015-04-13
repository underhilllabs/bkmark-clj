(ns bkmarker.views.bookmarks
  (:require [hiccup.core :refer :all]
            [hiccup.form :refer :all]
            [noir.session :as session]
            [bkmarker.db.dbconn :refer [db-spec]]
            [yesql.core :refer [defqueries]]
            [ring.util.request :refer [path-info]]
            [bkmarker.helpers :refer [get-gravatar-pic]]
            [hiccup.page :refer :all]))

;; load the yesql queries
(defqueries "bkmarker/models/user.sql")

(defn login-nav
  "Login/current session navigation widget"
  []
  (html [:ul.nav.navbar-nav.navbar-right
         [:li
          (if-let [username (session/get :user-name)]
            [:a {:href "/profile/"} (str "Logged in as: " username)]
            [:a {:href "/login"} "Login"])]]))

(defn nav-links-auth
  [user-name]
  (html
   [:li [:a {:href (str "/bookmarks/user/" user-name)} "My Bookmarks"]]
   [:li [:a {:href "/bookmarks/new"} "Add Bookmark"]]))

(defn nav-header
  "site navigation header for the web site"
  []
  (html [:div {:class "navbar navbar-fixed-top"}
   [:div {:class "navbar-inner"}
    [:div {:class "container"}
     [:a {:class "brand" :href "/"} "Bkmarker"]
     [:div {:class "nav-collapse"}
      [:ul {:class "nav"}
       (when-let [user-name (session/get :user-name)]
         (nav-links-auth user-name))
       [:li [:a {:href "/user/"} "Users"]]
       [:li [:a {:href "/tags/"} "Tags"]]]
      [:div 
       {:class "navbar-search"}
       (form-to 
        {:class "navbar-search pull-left"}
        [:get "/search/"]
        (text-field {:class "input-text span2" :placeholder "search"} "keywords"))]
      (login-nav)
      ]]]]))

(defn main-layout
  ([title content] (main-layout title content ""))
  ([title content pagination]
   "main-layout without pagination"
   (html
    [:head 
     [:title "Bkmarker: Social Bookmarking"]
     (include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css")
     (include-css "/css/styles.css")]
    [:body 
     (nav-header)
     [:div {:class "container"}
      [:div {:class "main-wrapper"} 
       [:h2 (h title)]
       content
       [:div {:id "pagination"} pagination]]]])))

(defn tag-links [bkmark]
  (map #(str "<a href='/tag/name/" % "'>" % "</a>") 
       (map #(:name %) 
            (get bkmark :tags))))

(defn view-bookmark [bkmark]
  (let [{:keys [title username url updated_at]} bkmark
        my-tags (tag-links bkmark)
        df (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm:ss:S")]
  (html
   [:div {:class "bookmark-wrapper"}
    [:div {:class "bookmark-title"} 
     [:a {:href url} (h title)]]
    [:div {:class "bookmark-tags"} my-tags]
    [:div {:class "bookmark-details"}
     (str (.format (java.text.SimpleDateFormat. "MMMMM dd, yyyy") updated_at) " by " )
     [:a {:href (str "/user/" username)} (h username)]]])))

(defn view-user-bookmark-count
  "view users and links to profiles and bookmarks with bkmk count"
  [user-count]
  (let [{:keys [id username count pic_url fullname email]} user-count]
    (html [:div {:class "user-div"}
           [:span {:class "profile-avatar"} 
            [:img {:src (get-gravatar-pic email)}]]
           [:span {:class "profile-username"} 
            [:a {:href (str "/profile/" id) } username]]
           [:span {:class "profile-fullname"} 
            (str " " fullname " ")
            [:a {:href (str "/user/" username) } (str "View Bookmarks (" count ")")]]])))

(defn view-tag-count
  [tags-count]
  (let [{:keys [name count]} tags-count]
    (html [:div {:class "tag-div"}
           [:a {:href (str "/tag/name/" name)} name]
           (str " (" count  ")")])))

(defn view-pagination
  [url limit page total]
  (let [total-pages (/ total limit)
        low-page (+ (* (quot page 10) 10) 1) 
        hi-page (+ low-page 10)]
    (str [:a {:href  (str url "?page=" 1)} "<<"]
         (apply str 
                (map 
                 #(str [:a {:href  (str url "?page=" %)} page]) 
                 (range low-page hi-page)))
         [:a {:href  (str url "?page=" total-pages)} ">>"])))

(defn view-pagination-simple
  [url page-num]
  (let [param-connecter (if (re-seq #"\?" url) "&" "?")] 
    [:div.row [:div.span9.pagination.flickr_pagination
               (if (> page-num 1) 
                 [:span {:class "previous"} [:a {:href  (str url param-connecter "page=" (dec page-num))} "<< earlier "]])
               [:span {:class "current-page"} page-num] 
               [:span {:class "next"} [:a {:href  (str url param-connecter "page=" (inc page-num))} " later >>"]]]]))

(defn view-login-page
  []
  (main-layout 
   "Sign In"
   (form-to 
    [:post "login"]
    [:div.field 
     [:label {:for "user-name"} "Email"]
      [:input {:class "input-text span2" :name "username"}]]
    [:div.field 
     [:label {:for "user-pass"} "Password"]
     [:input {:type "password" 
              :class "input-text span2" 
              :name "password"}]]
    [:div.actions (submit-button "Login")])))

(defn view-user-popular-tags
  [user-id]
  (let [tags (find-user-tags-count db-spec user-id 0 10)]
    [:div
     [:ul.tags
      (for [tag tags]
        [:li.popular_tags
         (str (get tag :name) " (" (get tag :count) ")" )])]]))

(defn view-user-recent-bookmarks
  [user-id]
  (let [bookmarks (find-bookmarks-user db-spec user-id 0 10)]
    [:div.rec-bkmks
     [:ul.bookmarks
      (for [bookmark bookmarks]
        [:li.recent-bookmark
         [:a {:href (get bookmark :url) } (get bookmark :title)]])]]))

(defn view-profile
  [user]
  (main-layout
   (str "Profile of " (user :username))
   [:div.profile
    [:img {:src (get-gravatar-pic (user :email))}]
    [:span "Name:"]
    [:span (user :fullname)]
    (when (= (user :id) (session/get :user-id))
      [:div
       [:hr]
       [:h3 "Add a bookmarklet to browser"]
       [:span "Drag the following shortcut to your browser bookmark bar for a shortcut to bookmark the current page:"]
       [:p 
        [:span.bookmarklet
         [:a {:href "javascript:x=document;a=encodeURIComponent(x.location.href);t=encodeURIComponent(x.title);d=encodeURIComponent(window.getSelection());open('http://dev.bkmark.me/bookmarklet?action=add&popup=1&address='+a+'&title='+t+'&description='+d,'Bkmarker%20Bookmarks','modal=1,status=0,scrollbars=1,toolbar=0,resizable=1,width=730,height=465,left='+(screen.width-730)/2+',top='+(screen.height-425)/2);void%200;"} "Post to Bookmarks"]
        ]]])
    [:hr]
    [:div.recent
     [:h3 "Recent Bookmarks"]
     (view-user-recent-bookmarks (user :id))]
    [:hr]
    [:div.pop-tags
     [:h3 "Popular Tags"]
     (view-user-popular-tags (user :id))]
    [:hr]]))

(defn view-register-page
  []
  (main-layout 
   "Register New Account"
   (form-to 
    [:post "/register"]
    [:div.field 
     [:label {:for "username"} "Username"]
      [:input {:class "input-text span2" :name "username"}]]
    [:div.field 
     [:label {:for "email"} "Email"]
      [:input {:class "input-text span2" :name "email"}]]
    [:div.field 
     [:label {:for "userpass"} "Password"]
     [:input {:type "password" 
              :class "input-text span2" 
              :name "userpass"}]]
    [:div.field 
     [:label {:for "userpass2"} "Confirm Password"]
     [:input {:type "password" 
              :class "input-text span2" 
              :name "userpass2"}]]
    [:div.actions (submit-button "Register")])))

(defn view-bookmark-form
  [user-id]
  (main-layout 
   "Add Bookmark"
   (form-to 
    [:post "/bookmarks/new"]
    [:input {:type "hidden" :value user-id :name "user_id"}] 
    [:div.field 
     [:label {:for "address"} "Address"]
      [:input {:class "input-text span2" :name "address"}]]
    [:div.field 
     [:label {:for "title"} "Title"]
      [:input {:class "input-text span2" :name "title"}]]
    [:div.field 
     [:label {:for "description"} "Description"]
      [:input {:class "input-text span2" :name "description"}]]
    [:div.field 
     [:label {:for "tags"} "Tags"]
      [:input {:class "input-text span2" :name "tags"}]]
    [:div.field 
     [:label {:for "private"} "Private"]
     [:input {:type "checkbox" 
              :class "input-checkbox" 
              :name "private"}]]
    [:div.field 
     [:label {:for "archive-url"} "archive url?"]
     [:input {:type "checkbox" 
              :class "input-checkbox" 
              :name "archive_url"}]]
    [:div.actions (submit-button "Create Bookmark")])))

(defn view-bookmarklet-form
  [user-id params]
  (main-layout 
   "Add Bookmark"
   (form-to 
    [:post "/bookmarks/new"]
    [:input {:type "hidden" :value user-id :name "user_id"}] 
    [:input {:type "hidden" :value "true" :name "bookmarklet"}] 
    [:div.field 
     [:label {:for "address"} "Address"]
      [:input {:class "input-text span2" :name "address" :value (params "address")}]]
    [:div.field 
     [:label {:for "title"} "Title"]
      [:input {:class "input-text span2" :name "title" :value (params "title")}]]
    [:div.field 
     [:label {:for "description"} "Description"]
      [:input {:class "input-text span2" :name "description" :value (params "description")}]]
    [:div.field 
     [:label {:for "tags"} "Tags"]
      [:input {:class "input-text span2" :name "tags"}]]
    [:div.field 
     [:label {:for "private"} "Private"]
     [:input {:type "checkbox" 
              :class "input-checkbox" 
              :name "private"}]]
    [:div.field 
     [:label {:for "archive-url"} "archive url?"]
     [:input {:type "checkbox" 
              :class "input-checkbox" 
              :name "archive_url"}]]
    [:div.actions (submit-button "Create Bookmark")])))
