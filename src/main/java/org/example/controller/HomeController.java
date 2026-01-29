package org.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return """
               <!DOCTYPE html>
               <html>
               <head>
                   <title>Rose Game API</title>
                   <style>
                       body {
                           font-family: 'Arial', sans-serif;
                           margin: 0;
                           padding: 20px;
                           background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                           color: white;
                           min-height: 100vh;
                       }
                       .container {
                           max-width: 800px;
                           margin: 0 auto;
                           background: rgba(255, 255, 255, 0.1);
                           backdrop-filter: blur(10px);
                           padding: 30px;
                           border-radius: 15px;
                           box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
                       }
                       h1 {
                           color: #ffd700;
                           text-align: center;
                           font-size: 2.5em;
                           margin-bottom: 30px;
                           text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
                       }
                       .card {
                           background: rgba(255, 255, 255, 0.15);
                           border-radius: 10px;
                           padding: 20px;
                           margin: 15px 0;
                           transition: transform 0.3s;
                       }
                       .card:hover {
                           transform: translateY(-5px);
                           background: rgba(255, 255, 255, 0.2);
                       }
                       a {
                           color: #ffd700;
                           text-decoration: none;
                           font-weight: bold;
                           display: block;
                           padding: 10px;
                           border: 2px solid #ffd700;
                           border-radius: 5px;
                           text-align: center;
                           margin: 5px 0;
                           transition: all 0.3s;
                       }
                       a:hover {
                           background: #ffd700;
                           color: #333;
                       }
                       .status {
                           color: #90ee90;
                           font-weight: bold;
                       }
                       .badge {
                           display: inline-block;
                           background: #ff4757;
                           color: white;
                           padding: 3px 8px;
                           border-radius: 12px;
                           font-size: 0.8em;
                           margin-left: 10px;
                       }
                   </style>
               </head>
               <body>
                   <div class="container">
                       <h1>🎮 ROSE GAME API</h1>
                       
                       <div class="card">
                           <h2>🚀 Статус системы</h2>
                           <p>✅ <span class="status">АКТИВЕН</span> - Приложение успешно запущено</p>
                           <p>⏱️ Время запуска: 19.348 секунд</p>
                           <p>🔒 Безопасность: JWT аутентификация</p>
                       </div>
                       
                       <div class="card">
                           <h2>📚 Документация API</h2>
                           <a href="/swagger-ui/index.html" target="_blank">
                               📖 Swagger UI - Интерактивная документация
                           </a>
                           <a href="/v3/api-docs" target="_blank">
                               📄 OpenAPI Specification
                           </a>
                       </div>
                       
                       <div class="card">
                           <h2>🔧 Мониторинг и утилиты</h2>
                           <a href="/actuator/health" target="_blank">
                               🏥 Health Check - Состояние системы
                           </a>
                           <a href="/actuator/info" target="_blank">
                               ℹ️ System Info - Информация о приложении
                           </a>
                           <a href="/h2-console" target="_blank">
                               🗄️ H2 Console - База данных (DEV)
                           </a>
                       </div>
                       
                       <div class="card">
                           <h2>🔐 Аутентификация</h2>
                           <p>Для доступа к защищенным эндпоинтам используйте:</p>
                           <a href="/api/auth/login">
                               🔑 Получить JWT токен
                           </a>
                           <p><small>Используйте заголовок: <code>Authorization: Bearer &lt;ваш_токен&gt;</code></small></p>
                       </div>
                       
                       <div class="card">
                           <h2>📊 Технические детали</h2>
                           <p>🖥️ Java 21.0.7 | 🍃 Spring Boot 3.3.2 | 🐳 Docker Ready</p>
                           <p>🗄️ MySQL Database | ⚡ Redis Cache | 🔒 JWT Security</p>
                           <p>🔄 74 эндпоинта | 🚀 Production Ready</p>
                       </div>
                       
                       <hr>
                       <p style="text-align: center; color: #ccc;">
                           🏆 Rose Game API v1.0.0 | 🚀 Готов к использованию
                       </p>
                   </div>
               </body>
               </html>
               """;
    }
}