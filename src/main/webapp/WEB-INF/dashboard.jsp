<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Game Dashboard - Rose Project</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            color: #333;
        }

        .dashboard-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }

        .header {
            background: rgba(255, 255, 255, 0.95);
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
            margin-bottom: 30px;
            text-align: center;
        }

        .header h1 {
            color: #764ba2;
            font-size: 2.5em;
            margin-bottom: 10px;
        }

        .header p {
            color: #666;
            font-size: 1.1em;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .stat-card {
            background: rgba(255, 255, 255, 0.95);
            padding: 25px;
            border-radius: 15px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            text-align: center;
            transition: transform 0.3s ease;
        }

        .stat-card:hover {
            transform: translateY(-5px);
        }

        .stat-card h3 {
            color: #764ba2;
            font-size: 2em;
            margin-bottom: 10px;
        }

        .stat-card p {
            color: #666;
            font-weight: 500;
        }

        .charts-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        .chart-container {
            background: rgba(255, 255, 255, 0.95);
            padding: 25px;
            border-radius: 15px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
        }

        .chart-container h3 {
            color: #764ba2;
            margin-bottom: 15px;
            text-align: center;
        }

        .database-section {
            background: rgba(255, 255, 255, 0.95);
            padding: 25px;
            border-radius: 15px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            margin-bottom: 30px;
        }

        .database-section h3 {
            color: #764ba2;
            margin-bottom: 20px;
            text-align: center;
        }

        .schema-diagram {
            display: flex;
            justify-content: center;
            flex-wrap: wrap;
            gap: 20px;
        }

        .table-card {
            background: #f8f9fa;
            border: 2px solid #764ba2;
            border-radius: 10px;
            padding: 15px;
            min-width: 200px;
        }

        .table-card h4 {
            color: #764ba2;
            margin-bottom: 10px;
            text-align: center;
            border-bottom: 1px solid #ddd;
            padding-bottom: 5px;
        }

        .table-column {
            font-size: 0.9em;
            margin: 5px 0;
            padding: 2px 5px;
            background: #e9ecef;
            border-radius: 3px;
        }

        .primary-key {
            color: #dc3545;
            font-weight: bold;
        }

        .foreign-key {
            color: #007bff;
            font-style: italic;
        }

        .navigation {
            text-align: center;
            margin-top: 20px;
        }

        .nav-button {
            background: #764ba2;
            color: white;
            padding: 12px 30px;
            border: none;
            border-radius: 25px;
            cursor: pointer;
            font-size: 1em;
            margin: 0 10px;
            transition: background 0.3s ease;
        }

        .nav-button:hover {
            background: #5a3790;
        }

        @media (max-width: 768px) {
            .charts-grid {
                grid-template-columns: 1fr;
            }

            .chart-container {
                min-width: 100%;
            }
        }
    </style>
</head>
<body>
<div class="dashboard-container">
    <div class="header">
        <h1>🎮 Rose Project Dashboard</h1>
        <p>Real-time visualization of game data and database structure</p>
    </div>

    <div class="stats-grid">
        <div class="stat-card">
            <h3 id="totalUsers">0</h3>
            <p>Total Users</p>
        </div>
        <div class="stat-card">
            <h3 id="activeUsers">0</h3>
            <p>Active Users</p>
        </div>
        <div class="stat-card">
            <h3 id="totalGames">0</h3>
            <p>Total Games Played</p>
        </div>
        <div class="stat-card">
            <h3 id="averageScore">0</h3>
            <p>Average Score</p>
        </div>
    </div>

    <div class="charts-grid">
        <div class="chart-container">
            <h3>Daily Active Users</h3>
            <canvas id="usersChart"></canvas>
        </div>
        <div class="chart-container">
            <h3>Game Type Distribution</h3>
            <canvas id="gamesChart"></canvas>
        </div>
    </div>

    <div class="database-section">
        <h3>📊 Database Schema</h3>
        <div class="schema-diagram">
            <div class="table-card">
                <h4>users</h4>
                <div class="table-column primary-key">id (PK)</div>
                <div class="table-column">username</div>
                <div class="table-column">password</div>
                <div class="table-column">email</div>
                <div class="table-column">created_at</div>
                <div class="table-column">last_login</div>
                <div class="table-column">is_active</div>
            </div>

            <div class="table-card">
                <h4>game_sessions</h4>
                <div class="table-column primary-key">id (PK)</div>
                <div class="table-column foreign-key">user_id (FK)</div>
                <div class="table-column">game_type</div>
                <div class="table-column">score</div>
                <div class="table-column">level</div>
                <div class="table-column">duration</div>
                <div class="table-column">created_at</div>
                <div class="table-column">completed_at</div>
            </div>

            <div class="table-card">
                <h4>achievements</h4>
                <div class="table-column primary-key">id (PK)</div>
                <div class="table-column foreign-key">user_id (FK)</div>
                <div class="table-column">achievement_name</div>
                <div class="table-column">achievement_description</div>
                <div class="table-column">unlocked_at</div>
            </div>
        </div>
    </div>

    <div class="navigation">
        <button class="nav-button" onclick="location.href='/rose/welcome'">🏠 Home</button>
        <button class="nav-button" onclick="location.href='/rose/game'">🎮 Play Game</button>
        <button class="nav-button" onclick="refreshData()">🔄 Refresh Data</button>
    </div>
</div>

<script>
    // Загрузка данных с сервера
    async function loadDashboardData() {
        try {
            const response = await fetch('/rose/dashboard?action=data');
            const data = await response.json();
            updateDashboard(data);
        } catch (error) {
            console.error('Error loading dashboard data:', error);
        }
    }

    // Обновление интерфейса
    function updateDashboard(data) {
        // Обновление статистики
        document.getElementById('totalUsers').textContent = data.totalUsers;
        document.getElementById('activeUsers').textContent = data.activeUsers;
        document.getElementById('totalGames').textContent = data.totalGames;
        document.getElementById('averageScore').textContent = data.averageScore;

        // График пользователей
        const usersCtx = document.getElementById('usersChart').getContext('2d');
        new Chart(usersCtx, {
            type: 'line',
            data: {
                labels: Object.keys(data.dailyUsers),
                datasets: [{
                    label: 'Daily Active Users',
                    data: Object.values(data.dailyUsers),
                    borderColor: '#764ba2',
                    backgroundColor: 'rgba(118, 75, 162, 0.1)',
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'top',
                    }
                }
            }
        });

        // График распределения игр
        const gamesCtx = document.getElementById('gamesChart').getContext('2d');
        new Chart(gamesCtx, {
            type: 'doughnut',
            data: {
                labels: Object.keys(data.gameDistribution),
                datasets: [{
                    data: Object.values(data.gameDistribution),
                    backgroundColor: [
                        '#764ba2',
                        '#667eea',
                        '#f093fb',
                        '#4facfe'
                    ],
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'bottom',
                    }
                }
            }
        });
    }

    function refreshData() {
        loadDashboardData();
        alert('Data refreshed!');
    }

    // Загрузка данных при старте
    document.addEventListener('DOMContentLoaded', loadDashboardData);

    // Авто-обновление каждые 30 секунд
    setInterval(loadDashboardData, 30000);
</script>
</body>
</html>