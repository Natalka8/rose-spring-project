-- Создание базы данных (уже создана через Docker, но на всякий случай)
CREATE DATABASE IF NOT EXISTS rose_quest CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE rose_quest;

-- Таблица пользователей (если нужно хранить в БД)
CREATE TABLE IF NOT EXISTS users (
                                     id INT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE
    );

-- Таблица сохранений игры
CREATE TABLE IF NOT EXISTS game_saves (
                                          id INT AUTO_INCREMENT PRIMARY KEY,
                                          username VARCHAR(100) NOT NULL,
    current_location VARCHAR(100) NOT NULL DEFAULT 'start',
    inventory_lantern BOOLEAN DEFAULT FALSE,
    inventory_scroll BOOLEAN DEFAULT FALSE,
    inventory_amulet BOOLEAN DEFAULT FALSE,
    score INT DEFAULT 0,
    saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_username (username)
    );

-- Таблица статистики игры
CREATE TABLE IF NOT EXISTS game_stats (
                                          id INT AUTO_INCREMENT PRIMARY KEY,
                                          username VARCHAR(100) NOT NULL,
    completed BOOLEAN DEFAULT FALSE,
    completion_time INT DEFAULT 0,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    score INT DEFAULT 0
    );

-- Таблица достижений игроков
CREATE TABLE IF NOT EXISTS player_achievements (
                                                   id INT AUTO_INCREMENT PRIMARY KEY,
                                                   username VARCHAR(100) NOT NULL,
    achievement_name VARCHAR(100) NOT NULL,
    achievement_description TEXT,
    unlocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Вставка тестовых данных
INSERT IGNORE INTO users (username, password, email) VALUES
('admin', 'admin', 'admin@example.com'),
('test_user', 'password', 'test@example.com');

INSERT IGNORE INTO game_stats (username, completed, completion_time, score, completed_at) VALUES
('admin', TRUE, 1800, 300, NOW()),
('admin', FALSE, 900, 150, NOW()),
('test_user', TRUE, 2400, 250, NOW());

INSERT IGNORE INTO player_achievements (username, achievement_name, achievement_description) VALUES
('admin', 'Первопроходец', 'Завершил квест впервые'),
('admin', 'Собиратель', 'Собрал все артефакты'),
('test_user', 'Первопроходец', 'Завершил квест впервые');

-- Создание индексов для улучшения производительности
CREATE INDEX idx_username ON game_saves(username);
CREATE INDEX idx_stats_username ON game_stats(username);
CREATE INDEX idx_stats_completed ON game_stats(completed);
CREATE INDEX idx_achievements_username ON player_achievements(username);