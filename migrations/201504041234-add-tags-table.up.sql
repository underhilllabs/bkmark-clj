CREATE TABLE tags (
       id INTEGER PRIMARY KEY AUTO_INCREMENT,
       name VARCHAR(255),
       user_id INTEGER,
       bookmark_id INTEGER,
       created_at DATETIME,
       updated_at DATETIME
);
