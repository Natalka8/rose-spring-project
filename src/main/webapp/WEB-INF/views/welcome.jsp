<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>Добро пожаловать в Текстовый квест</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .story {
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .start-btn {
            display: inline-block;
            margin-top: 20px;
            padding: 10px 20px;
            background-color: #4CAF50;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            font-weight: bold;
        }
        .start-btn:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
<div class="story">
    <h1>Тайна Забытого Леса</h1>

    <p>Давным-давно, когда магия еще была частью этого мира, в глубине древнего леса
        был спрятан могущественный артефакт - Камень Вечности. Говорят, тот, кто найдет его,
        получит власть над временем и пространством.</p>

    <p>Многие искатели приключений пытались найти этот камень, но никто не возвращался
        из Забытого Леса. Легенды гласят, что лес испытывает искателей, предлагая им выбор
        на каждом шагу, и только самый мудрый и удачливый сможет добраться до цели.</p>

    <p>Теперь и ваша очередь попытать счастья. Но будьте осторожны - лес полон загадок
        и опасностей. Каждое ваше решение будет влиять на исход путешествия.</p>

    <p>Сможете ли вы найти Камень Вечности и раскрыть его тайну?</p>

    <a href="login.jsp" class="start-btn">Начать приключение</a>
</div>
</body>
</html>
