<!DOCTYPE html>
<html>
<head>
    <title>Rose Project</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 40px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            text-align: center;
        }
        .container {
            max-width: 800px;
            margin: 100px auto;
            background: rgba(255,255,255,0.1);
            padding: 40px;
            border-radius: 15px;
            backdrop-filter: blur(10px);
        }
        .nav a {
            display: inline-block;
            margin: 15px;
            padding: 15px 25px;
            background: rgba(255,255,255,0.2);
            color: white;
            text-decoration: none;
            border-radius: 8px;
            font-size: 1.1em;
            transition: background 0.3s;
        }
        .nav a:hover {
            background: rgba(255,255,255,0.3);
        }
        .status {
            background: rgba(76, 175, 80, 0.2);
            padding: 15px;
            border-radius: 8px;
            margin: 20px 0;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>🎮 Rose Project</h1>
    <div class="status">
        <h2>✅ Tomcat Server Running</h2>
        <p>Application successfully deployed on Tomcat 10.1.41</p>
    </div>

    <div class="nav">
        <a href="${pageContext.request.contextPath}/auth">🔐 Login</a>
        <a href="${pageContext.request.contextPath}/dashboard">📊 Dashboard</a>
        <a href="${pageContext.request.contextPath}/game">🎮 Game</a>
    </div>

    <div style="margin-top: 40px; text-align: left; background: rgba(255,255,255,0.1); padding: 20px; border-radius: 10px;">
        <h3>🚀 Quick Start:</h3>
        <p>1. Click <strong>Login</strong> to access the application</p>
        <p>2. Use credentials: <strong>admin / admin</strong></p>
        <p>3. Explore the Dashboard for data visualization</p>
        <p>4. Try the interactive Game</p>
    </div>

    <div style="margin-top: 30px; font-size: 0.9em; opacity: 0.8;">
        <p>Tomcat 10.1.41 | Java Web Application | Spring & Hibernate</p>
    </div>
</div>
</body>
</html>