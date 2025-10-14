<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Статистика игроков</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 40px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            min-height: 100vh;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background: rgba(255,255,255,0.95);
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 8px 32px rgba(0,0,0,0.1);
            color: #333;
        }
        h1 {
            color: #764ba2;
            text-align: center;
            margin-bottom: 30px;
        }
        .stats-card {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 10px;
            margin: 20px 0;
            border-left: 4px solid #764ba2;
        }
        .back-link {
            display: inline-block;
            padding: 12px 24px;
            background: #764ba2;
            color: white;
            text-decoration: none;
            border-radius: 8px;
            margin: 10px 5px;
            transition: background 0.3s;
        }
        .back-link:hover {
            background: #5a3790;
        }
        .success {
            color: #4CAF50;
            font-size: 18px;
            text-align: center;
            margin: 20px 0;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>📊 Статистика игроков</h1>

    <div class="success">
        ✅ ${message}
    </div>

    <div class="stats-card">
        <h3>Общая статистика</h3>
        <p>Всего игроков: <strong>${totalPlayers}</strong></p>
        <p>Активных игроков: <strong>3</strong></p>
        <p>Всего сыграно игр: <strong>15</strong></p>
    </div>

    <div style="text-align: center; margin-top: 30px;">
        <a href="welcome" class="back-link">← Назад в игру</a>
        <a href="dashboard" class="back-link">📊 Общая статистика</a>
        <a href="auth?logout=true" class="back-link">🚪 Выйти</a>
    </div>
</div>
</body>
</html>