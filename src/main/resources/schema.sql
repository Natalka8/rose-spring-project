-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
    );

-- Таблица игровых сессий
CREATE TABLE IF NOT EXISTS game_sessions (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             user_id BIGINT NOT NULL,
                                             game_type VARCHAR(50) NOT NULL,
    score INT DEFAULT 0,
    level INT DEFAULT 1,
    duration INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- Таблица достижений
CREATE TABLE IF NOT EXISTS achievements (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            user_id BIGINT NOT NULL,
                                            achievement_name VARCHAR(100) NOT NULL,
    achievement_description TEXT,
    unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

-- Вставка тестовых данных
INSERT INTO users (username, password, email) VALUES
                                                  ('admin', 'admin123', 'admin@example.com'),
                                                  ('user1', 'pass123', 'user1@example.com'),
                                                  ('gamer', 'game123', 'gamer@example.com');

INSERT INTO game_sessions (user_id, game_type, score, level, duration) VALUES
                                                                           (1, 'puzzle', 1500, 5, 3600),
                                                                           (2, 'action', 2300, 8, 4200),
                                                                           (3, 'strategy', 1800, 6, 2900);

INSERT INTO achievements (user_id, achievement_name, achievement_description) VALUES
                                                                                  (1, 'First Steps', 'Complete your first game'),
                                                                                  (2, 'Speed Runner', 'Complete a game in under 10 minutes'),
                                                                                  (3, 'High Scorer', 'Score over 1500 points');