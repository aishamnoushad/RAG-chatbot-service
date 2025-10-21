CREATE TABLE chat_sessions (
                              id SERIAL PRIMARY KEY,
                              user_id VARCHAR(255) NOT NULL,
                              title VARCHAR(255) NOT NULL,
                              is_favorite BOOLEAN DEFAULT FALSE,
                              created_at TIMESTAMP NOT NULL DEFAULT now(),
                              updated_at TIMESTAMP NOT NULL DEFAULT now()
);