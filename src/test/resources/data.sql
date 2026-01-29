-- Тестовые пользователи
INSERT INTO users (username, email, password, level, experience, gold) VALUES
                                                                           ('test_player', 'test@example.com', '$2a$10$testhashfortesting123456789', 10, 1000, 500),
                                                                           ('admin_test', 'admin@test.com', '$2a$10$adminhashfortesting12345678', 50, 50000, 10000);

-- Тестовые задачи
INSERT INTO tasks (title, description, status, priority, user_id, due_date) VALUES
                                                                                ('Test Task 1', 'Test description 1', 'PENDING', 'MEDIUM', 1, DATEADD('DAY', 1, CURRENT_TIMESTAMP)),
                                                                                ('Test Task 2', 'Test description 2', 'IN_PROGRESS', 'HIGH', 1, DATEADD('DAY', 2, CURRENT_TIMESTAMP)),
                                                                                ('Test Task 3', 'Test description 3', 'COMPLETED', 'LOW', 2, DATEADD('DAY', -1, CURRENT_TIMESTAMP));