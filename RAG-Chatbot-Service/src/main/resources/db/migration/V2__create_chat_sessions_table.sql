CREATE TABLE chat_messages (
                              id SERIAL PRIMARY KEY,
                              session_id INT NOT NULL REFERENCES chat_sessions(id) ON DELETE CASCADE,
                              sender VARCHAR(50) NOT NULL,
                              content TEXT NOT NULL,
                              context TEXT,
                              created_at TIMESTAMP NOT NULL DEFAULT now()
);

