<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Результат - Текстовый квест</title>
</head>
<body>
<h1>${message}</h1>
<p>Сыграно игр: ${user.gamesPlayed}</p>
<form action="game" method="get">
    <button type="submit">Играть снова</button>
</form>
<hr>
<a href="logout">Выйти</a>
</body>
</html>