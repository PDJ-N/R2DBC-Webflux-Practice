DROP TABLE posts;
DROP TABLE users;

-- user 테이블
CREATE TABLE IF NOT EXISTS users
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    name     VARCHAR(100) NOT NULL,
    email    VARCHAR(100) NOT NULL UNIQUE,
    roles    VARCHAR(10) NOT NULL
);

-- posts 테이블
CREATE TABLE IF NOT EXISTS posts
(
    id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    title   VARCHAR(255) NOT NULL,
    content VARCHAR(255) NOT NULL,
    user_id BIGINT       NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);
