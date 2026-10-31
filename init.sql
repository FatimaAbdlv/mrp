CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL
);

CREATE TABLE media_entries (
                               id SERIAL PRIMARY KEY,
                               title VARCHAR(200) NOT NULL,
                               description TEXT,
                               media_type VARCHAR(20) NOT NULL,
                               release_year INTEGER NOT NULL,
                               genres VARCHAR(255),
                               age_restriction INTEGER NOT NULL,
                               creator_id INTEGER NOT NULL REFERENCES users(id)
);