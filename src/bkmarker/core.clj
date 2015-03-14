(ns bkmarker.core)
(use 'bkmarker.models.model)
(require '[clojure.java.jdbc :as j])

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn pr-users
  "Let's print all the user names!"
  []
  (j/query-results rs ["select * from users"] 4
     ; rs will be a sequence of maps, 
     ; one for each record in the result set. 
     (dorun (map #(println (:username %)) rs))))

