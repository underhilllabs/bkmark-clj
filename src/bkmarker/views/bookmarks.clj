(ns bkmarker.views.bookmarks
  (:require [hiccup.core :refer :all]
            [hiccup.page :refer :all]))

(defn main-layout [title content]
  (html
    [:head 
     [:title "Bkmarker: Social Bookmarking"]
     (include-css "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css")
     (include-css "/css/styles.css")]
    [:body 
     [:div {:class "navbar navbar-fixed-top"}
      [:div {:class "navbar-inner"}
       [:div {:class "container-fluid"}
        [:a {:class "brand" :href "/"} "Bkmarker"] 
      ]]]
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
  [{:keys [username count]} user-count]
  [:div {:class "user-details"}
   [:a {:href (str "/user/" username) } username]
   [:a {:href (str "/user/" username) } (str "View Bookmarks (" count ")")]])

