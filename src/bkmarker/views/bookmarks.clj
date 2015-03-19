(ns bkmarker.views.bookmarks
  (:require [hiccup.core :refer :all]
            [hiccup.form :refer :all]
            [bkmarker.helpers :refer [get-gravatar-pic]]
            [hiccup.page :refer :all]))

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
      ]]]]))
  
(defn main-layout [title content]
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
       content]]]))

(defn tag-links [bkmark]
  (map #(str "<a href='/tag/name/" % "'>" % "</a>") 
       (map #(:name %) 
            (get bkmark :tags))))

(defn view-bookmark [bkmark]
  (let [{:keys [title username url updated_at]} bkmark
        my-tags (tag-links bkmark)]
  (html
   [:div {:class "bookmark-wrapper"}
    [:div {:class "bookmark-title"} 
     [:a {:href url} (h title)]]
    [:div {:class "bookmark-tags"} my-tags]
    [:div {:class "bookmark-details"}
     (str updated_at " by " )
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
