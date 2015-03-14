(ns bkmarker.models.model)
(use 'bkmarker.db.dbconn)
(use 'korma-core)

(declare users bookmarks)

(defentity users
  (has-many bookmarks {:fk :bookmark_id})

(defentity bookmarks
  (belongs-to user {:fk :user_id}))

(defn pr-user-bkmarks
  "Let's print users"
  [name num]
  (select users
          (with bookmarks)
          (where {:name name})
          (limit num)))

