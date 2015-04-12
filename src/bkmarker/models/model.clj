(ns bkmarker.models.model
  (:require [bkmarker.db.dbconn :refer :all]
            [korma.core :refer :all]))

(declare users bookmarks tags)

(defentity users
  (has-many bookmarks {:fk :user_id})
  (has-many tags {:fk :user_id}))

(defentity bookmarks
  (belongs-to users {:fk :user_id})
  (has-many tags {:fk :bookmark_id}))

(defentity tags
  (belongs-to users {:fk :user_id})
  (belongs-to bookmarks {:fk :bookmark_id}))
  

(defn pr-user-bkmarks
  "Select the users and their bookmark count"
  [name num]
  (select users
          (with bookmarks)
          (where {:name name})
          (limit num)))

(defn find-user-email
  "Select user with email"
  [email]
  (select users
          (where {:email email})
          (limit 1)))

(defn find-user-by-id
  "Select user with email"
  [user-id]
  (select users
          (where {:id user-id})
          (limit 1)))

(defn create-user
  "Create a new user. Returns the id."
  [username email pass_digest]
  (let [user-return 
        (insert users
                (values [{:username username :email email :password_digest pass_digest}]))]
    (user-return :generated_key)))

(defn bkmarks-query
  [lim off]
  (select bookmarks 
          (with users)
          (with tags)
          (order :updated_at :DESC)
          (limit lim)
          (offset off)))

(defn bkmarks-user-query
  "Select the bookmarks for a User"
  [my-username lim off]
  (select bookmarks 
          (with users)
          (with tags)
          (where {:users.username my-username})
          (order :updated_at :DESC)
          (limit lim)
          (offset off)))

(defn users-bookmark-count-query
  "Returns user details and their bookmark count"
  []
  (select users
          (fields :id :username :fullname :pic_url :email)
          (join bookmarks (= :bookmarks.user_id :id))
          (aggregate (count :bookmarks.id) :count)
          (group :bookmarks.user_id)
          (order :count :DESC)))

(defn tags-count-query
  "Returns tags details and each tags bookmark count"
  []
  (select tags
          (fields :name)
          (aggregate (count :name) :count)
          (group :name)
          (order :count :DESC)
          (limit 50)))

(defn bkmarks-tag-query
  [my-tag lim off]
  (select tags
          (with bookmarks)
          (with users)
          (where {:tags.name my-tag})
          ;;(order :updated_at :DESC)
          (limit lim)
          (offset off)))

(defn bkmarks-search-query
  "search bookmarks with title like search phrase"
  [phrase lim off]
  (select bookmarks
          (with tags)
          (where {:title [like (str "%" phrase "%")]})
          (order :updated_at :DESC)
          (limit lim)
          (offset off)))

