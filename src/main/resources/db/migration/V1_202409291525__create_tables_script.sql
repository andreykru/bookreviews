CREATE TABLE IF NOT EXISTS books
(
    id          VARCHAR(255) PRIMARY KEY,
    title       VARCHAR(255),
    author      VARCHAR(255),
    description TEXT
);

CREATE TABLE IF NOT EXISTS users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email    VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255)        NOT NULL
);

DROP INDEX IF EXISTS idx_users_username;
DROP INDEX IF EXISTS idx_users_email;
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);

CREATE TABLE IF NOT EXISTS reviews
(
    id          BIGSERIAL PRIMARY KEY,
    book_id     VARCHAR(255) REFERENCES books (id) ON DELETE CASCADE NOT NULL,
    user_id     BIGINT REFERENCES users (id) ON DELETE CASCADE       NOT NULL,
    review_text TEXT,
    created_at  TIMESTAMP(6),
    updated_at  TIMESTAMP(6)
);

DROP INDEX IF EXISTS unique_user_book;
CREATE UNIQUE INDEX unique_user_book ON reviews (user_id, book_id);
