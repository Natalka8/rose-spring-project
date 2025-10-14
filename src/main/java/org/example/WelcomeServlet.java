package org.example;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/welcome")
public class WelcomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }

        User user = (User) session.getAttribute("user");
        String contextPath = request.getContextPath();

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("    <title>Welcome - Rose Project</title>");
        html.append("    <style>");
        html.append("        body { ");
        html.append("            font-family: Arial, sans-serif; ");
        html.append("            margin: 0; ");
        html.append("            padding: 40px; ");
        html.append("            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);");
        html.append("            color: white;");
        html.append("            min-height: 100vh;");
        html.append("        }");
        html.append("        .container { ");
        html.append("            max-width: 800px; ");
        html.append("            margin: 0 auto; ");
        html.append("            background: rgba(255,255,255,0.95); ");
        html.append("            padding: 40px; ");
        html.append("            border-radius: 15px; ");
        html.append("            box-shadow: 0 8px 32px rgba(0,0,0,0.1); ");
        html.append("            color: #333;");
        html.append("        }");
        html.append("        .nav { ");
        html.append("            margin: 30px 0; ");
        html.append("            text-align: center;");
        html.append("        }");
        html.append("        .nav a { ");
        html.append("            display: inline-block; ");
        html.append("            margin: 10px; ");
        html.append("            padding: 15px 25px; ");
        html.append("            background: #764ba2; ");
        html.append("            color: white; ");
        html.append("            text-decoration: none; ");
        html.append("            border-radius: 8px; ");
        html.append("            transition: background 0.3s;");
        html.append("            font-size: 16px;");
        html.append("        }");
        html.append("        .nav a:hover { ");
        html.append("            background: #5a3790; ");
        html.append("        }");
        html.append("        .user-info { ");
        html.append("            background: #e3f2fd; ");
        html.append("            padding: 25px; ");
        html.append("            border-radius: 10px; ");
        html.append("            margin: 25px 0; ");
        html.append("            border-left: 5px solid #2196f3;");
        html.append("        }");
        html.append("        h1 { ");
        html.append("            color: #764ba2; ");
        html.append("            text-align: center;");
        html.append("            margin-bottom: 30px;");
        html.append("        }");
        html.append("        .welcome-message {");
        html.append("            text-align: center;");
        html.append("            font-size: 1.2em;");
        html.append("            margin-bottom: 30px;");
        html.append("            color: #666;");
        html.append("        }");
        html.append("        .game-options {");
        html.append("            display: grid;");
        html.append("            grid-template-columns: 1fr 1fr;");
        html.append("            gap: 15px;");
        html.append("            margin: 20px 0;");
        html.append("        }");
        html.append("        .game-option {");
        html.append("            background: #e8f5e8;");
        html.append("            padding: 20px;");
        html.append("            border-radius: 10px;");
        html.append("            text-align: center;");
        html.append("            border: 2px solid #4caf50;");
        html.append("        }");
        html.append("        .game-option h3 {");
        html.append("            color: #2e7d32;");
        html.append("            margin-top: 0;");
        html.append("        }");
        html.append("    </style>");
        html.append("</head>");
        html.append("<body>");
        html.append("    <div class=\"container\">");
        html.append("        <h1>🎉 Добро пожаловать в Rose Project!</h1>");
        html.append("        <div class=\"welcome-message\">");
        html.append("            Рады видеть вас, <strong>" + user.getUsername() + "</strong>!");
        html.append("        </div>");
        html.append("        ");
        html.append("        <div class=\"user-info\">");
        html.append("            <h3>📋 Информация о пользователе</h3>");
        html.append("            <p><strong>👤 Имя пользователя:</strong> " + user.getUsername() + "</p>");
        html.append("            <p><strong>📧 Email:</strong> " + (user.getEmail() != null ? user.getEmail() : "Не указан") + "</p>");
        html.append("            <p><strong>📅 Дата регистрации:</strong> " + user.getCreatedAt() + "</p>");
        html.append("            <p><strong>🟢 Статус:</strong> Активен</p>");
        html.append("        </div>");
        html.append("        ");
        html.append("        <!-- Новые опции игры -->");
        html.append("        <div class=\"game-options\">");
        html.append("            <div class=\"game-option\">");
        html.append("                <h3>🎮 Новая игра</h3>");
        html.append("                <p>Начать приключение с самого начала</p>");
        html.append("                <a href='" + contextPath + "/game' style='display: inline-block; margin-top: 10px; padding: 10px 20px; background: #4caf50; color: white; text-decoration: none; border-radius: 5px;'>Начать новую игру</a>");
        html.append("            </div>");
        html.append("            <div class=\"game-option\">");
        html.append("                <h3>⏯️ Продолжить</h3>");
        html.append("                <p>Вернуться к сохраненной игре</p>");
        html.append("                <a href='" + contextPath + "/game?action=continue' style='display: inline-block; margin-top: 10px; padding: 10px 20px; background: #2196f3; color: white; text-decoration: none; border-radius: 5px;'>Продолжить игру</a>");
        html.append("            </div>");
        html.append("        </div>");
        html.append("        ");
        html.append("        <div class=\"nav\">");
        html.append("            <a href='" + contextPath + "/game'>🎮 Играть в игру</a>");
        html.append("            <a href='" + contextPath + "/stats'>📊 Статистика</a>");
        html.append("            <a href='" + contextPath + "/dashboard'>📈 Панель управления</a>");
        html.append("            <a href='" + contextPath + "/logout'>🚪 Выйти</a>");
        html.append("        </div>");
        html.append("        ");
        html.append("        <div style=\"margin-top: 40px; padding: 20px; background: #f5f5f5; border-radius: 10px;\">");
        html.append("            <h3>🚀 О проекте Rose</h3>");
        html.append("            <p>Это веб-приложение демонстрирует:</p>");
        html.append("            <ul>");
        html.append("                <li>🔐 Аутентификацию пользователей</li>");
        html.append("                <li>💾 Управление сессиями</li>");
        html.append("                <li>🎮 Интерактивные игры с сохранением прогресса</li>");
        html.append("                <li>📊 Визуализацию данных и статистику</li>");
        html.append("                <li>🗄️ Интеграцию с базой данных MySQL</li>");
        html.append("                <li>💾 Авто-сохранение игрового прогресса</li>");
        html.append("            </ul>");
        html.append("        </div>");
        html.append("    </div>");
        html.append("</body>");
        html.append("</html>");

        response.getWriter().println(html.toString());
    }
}