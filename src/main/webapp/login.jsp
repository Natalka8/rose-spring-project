<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Вход - Текстовый квест</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 500px;
            margin: 0 auto;
            padding: 20px;
        }
        .back-link {
            display: block;
            margin-top: 20px;
        }
    </style>
</head>
<body>
<h1>Добро пожаловать в Текстовый квест</h1>
<c:if test="${not empty error}">
    <p style="color: red;">${error}</p>
</c:if>
<form action="auth" method="post">
    <div>
        <label for="username">Имя пользователя:</label>
        <input type="text" id="username" name="username" required>
    </div>
    <div>
        <label for="password">Пароль:</label>
        <input type="password" id="password" name="password" required>
    </div>
    <button type="submit">Войти</button>
</form>
<p>Используйте имя: 'user' и пароль: 'pass'</p>

<a href="welcome" class="back-link">← Вернуться к истории</a>
</body>
</html>