CREATE TABLE bookmarks (
       id INTEGER PRIMARY KEY AUTO_INCREMENT,
       url VARCHAR(255),
       title VARCHAR(255),
       description TEXT,
       private SMALLINT,
       created_at DATETIME,
       updated_at DATETIME,
       user_id INTEGER,
       hashed_url VARCHAR(255),
       archive_url VARCHAR(255),
       is_archived SMALLINT
);
