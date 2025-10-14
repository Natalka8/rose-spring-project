package org.example;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Простая аутентификация
        if ("admin".equals(username) && "admin".equals(password)) {
            HttpSession session = request.getSession();

            // Создаем пользователя и сохраняем в сессии
            User user = new User();
            user.setUsername(username);
            user.setLevel(5); // пример данных
            user.setHealth(100);
            user.setGold(500);
            user.setExperience(1000);

            session.setAttribute("user", user);
            session.setAttribute("username", username);

            // Перенаправляем на welcome
            response.sendRedirect("welcome"); // Убрал request.getContextPath()
        } else {
            // Если аутентификация не удалась
            showLoginPage(response, "Invalid username or password. Try 'admin/admin'", true);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Показываем форму логина
        showLoginPage(response, null, false);
    }

    private void showLoginPage(HttpServletResponse response, String errorMessage, boolean isError)
            throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("    <title>Login - Rose Project</title>");
        html.append("    <style>");
        html.append("        body { ");
        html.append("            font-family: Arial, sans-serif; ");
        html.append("            margin: 0; ");
        html.append("            padding: 40px; ");
        html.append("            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);");
        html.append("            color: white;");
        html.append("            display: flex;");
        html.append("            justify-content: center;");
        html.append("            align-items: center;");
        html.append("            min-height: 100vh;");
        html.append("        }");
        html.append("        .login-container { ");
        html.append("            max-width: 400px; ");
        html.append("            width: 100%;");
        html.append("            background: rgba(255,255,255,0.95); ");
        html.append("            padding: 40px; ");
        html.append("            border-radius: 15px; ");
        html.append("            box-shadow: 0 8px 32px rgba(0,0,0,0.1); ");
        html.append("            color: #333;");
        html.append("        }");
        html.append("        .form-group { ");
        html.append("            margin-bottom: 20px; ");
        html.append("        }");
        html.append("        label { ");
        html.append("            display: block; ");
        html.append("            margin-bottom: 8px; ");
        html.append("            font-weight: bold;");
        html.append("        }");
        html.append("        input[type=\"text\"], input[type=\"password\"] { ");
        html.append("            width: 100%; ");
        html.append("            padding: 12px; ");
        html.append("            border: 2px solid #ddd; ");
        html.append("            border-radius: 8px; ");
        html.append("            font-size: 16px;");
        html.append("            box-sizing: border-box;");
        html.append("        }");
        html.append("        input[type=\"text\"]:focus, input[type=\"password\"]:focus { ");
        html.append("            border-color: #764ba2; ");
        html.append("            outline: none; ");
        html.append("        }");
        html.append("        button { ");
        html.append("            background: #764ba2; ");
        html.append("            color: white; ");
        html.append("            padding: 15px 30px; ");
        html.append("            border: none; ");
        html.append("            border-radius: 8px; ");
        html.append("            font-size: 16px; ");
        html.append("            cursor: pointer; ");
        html.append("            width: 100%; ");
        html.append("            transition: background 0.3s;");
        html.append("        }");
        html.append("        button:hover { ");
        html.append("            background: #5a3790; ");
        html.append("        }");
        html.append("        .error { ");
        html.append("            background: #ffebee; ");
        html.append("            color: #c62828; ");
        html.append("            padding: 12px; ");
        html.append("            border-radius: 6px; ");
        html.append("            margin-bottom: 20px; ");
        html.append("            border-left: 4px solid #c62828;");
        html.append("        }");
        html.append("        .info { ");
        html.append("            background: #e3f2fd; ");
        html.append("            padding: 15px; ");
        html.append("            border-radius: 8px; ");
        html.append("            margin-top: 20px; ");
        html.append("            font-size: 14px; ");
        html.append("            border-left: 4px solid #2196f3;");
        html.append("        }");
        html.append("        h2 { ");
        html.append("            text-align: center; ");
        html.append("            margin-bottom: 30px; ");
        html.append("            color: #764ba2;");
        html.append("        }");
        html.append("    </style>");
        html.append("</head>");
        html.append("<body>");
        html.append("    <div class=\"login-container\">");
        html.append("        <h2>🔐 Вход в систему</h2>");

        if (errorMessage != null && isError) {
            html.append("        <div class=\"error\">");
            html.append("            <strong>Ошибка:</strong> " + errorMessage);
            html.append("        </div>");
        }

        html.append("        <form method=\"post\" action=\"auth\">"); // Явно указали action
        html.append("            <div class=\"form-group\">");
        html.append("                <label for=\"username\">Имя пользователя:</label>");
        html.append("                <input type=\"text\" id=\"username\" name=\"username\" required value=\"admin\">");
        html.append("            </div>");
        html.append("            <div class=\"form-group\">");
        html.append("                <label for=\"password\">Пароль:</label>");
        html.append("                <input type=\"password\" id=\"password\" name=\"password\" required value=\"admin\">");
        html.append("            </div>");
        html.append("            <button type=\"submit\">Вход</button>");
        html.append("        </form>");
        html.append("        <div class=\"info\">");
        html.append("            <strong>Тестовые учетные данные:</strong><br>");
        html.append("            Имя пользователя: <strong>admin</strong><br>");
        html.append("            Пароль: <strong>admin</strong>");
        html.append("        </div>");
        html.append("    </div>");
        html.append("</body>");
        html.append("</html>");

        response.getWriter().println(html.toString());
    }
}