CREATE TABLE users (
       id INTEGER primary key AUTO_INCREMENT,
       username VARCHAR(255),
       email VARCHAR(255),
       fullname VARCHAR(255),
       website VARCHAR(255),
       created_at DATETIME,
       pic_url VARCHAR(255),
       password_digest VARCHAR(255)
);
