<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Игра - Текстовый квест</title>
</head>
<body>
<h1>Текстовый квест</h1>
<p>Добро пожаловать, ${user.username}!</p>
<p>Сыграно игр: ${user.gamesPlayed}</p>
<hr>

<p>${message}</p>

<form action="game" method="get">
    <input type="hidden" name="step" value="${gameState}">
    <button type="submit" name="choice" value="1">${option1}</button>
    <button type="submit" name="choice" value="2">${option2}</button>
</form>

<hr>
<a href="logout">Выйти</a>
</body>
</html>