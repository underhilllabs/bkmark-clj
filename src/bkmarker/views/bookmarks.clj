(ns bkmarker.views.bookmarks
  (:require [hiccup.core :refer :all]
            [hiccup.form :refer :all]
            [ring.util.request :refer [path-info]]
            [bkmarker.helpers :refer [get-gravatar-pic]]
            [hiccup.page :refer :all]))

(defn login-nav
  "Login/current session navigation widget"
  []
  (html [:ul.nav.navbar-nav.navbar-right                                                                                                                   
   [:li [:a {:href "/login"} "Login"]]]))

(defn nav-header
  "site navigation header for the web site"
  []
  (html [:div {:class "navbar navbar-fixed-top"}
   [:div {:class "navbar-inner"}
    [:div {:class "container"}
     [:a {:class "brand" :href "/"} "Bkmarker"]
     [:div {:class "nav-collapse"}
      [:ul {:class "nav"}
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
  [user-count]
  (let [{:keys [username count pic_url fullname email]} user-count]
    (html [:div {:class "user-div"}
           [:span {:class "profile-avatar"} 
            [:img {:src (get-gravatar-pic email)}]]
           [:span {:class "profile-username"} 
            [:a {:href (str "/user/" username) } username]]
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

(defn view-register-page
  []
  (main-layout 
   "Register New Account"
   (form-to 
    [:post "/login"]
    [:div.field 
     [:label {:for "username"} "Email"]
      [:input {:class "input-text span2" :name "username"}]]
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
