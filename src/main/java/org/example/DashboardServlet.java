package org.example;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

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
        html.append("    <title>Dashboard - Rose Project</title>");
        html.append("    <style>");
        html.append("        body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; min-height: 100vh; }");
        html.append("        .container { max-width: 1200px; margin: 0 auto; background: rgba(255,255,255,0.95); padding: 30px; border-radius: 15px; color: #333; }");
        html.append("        .header { text-align: center; margin-bottom: 30px; }");
        html.append("        .header h1 { color: #764ba2; }");
        html.append("        .stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin-bottom: 30px; }");
        html.append("        .stat-card { background: white; padding: 25px; border-radius: 10px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); text-align: center; border-left: 5px solid #764ba2; }");
        html.append("        .stat-card h3 { color: #764ba2; font-size: 2em; margin: 0; }");
        html.append("        .nav { text-align: center; margin: 30px 0; }");
        html.append("        .nav a { display: inline-block; margin: 10px; padding: 12px 25px; background: #764ba2; color: white; text-decoration: none; border-radius: 6px; }");
        html.append("        .nav a:hover { background: #5a3790; }");
        html.append("    </style>");
        html.append("</head>");
        html.append("<body>");
        html.append("    <div class=\"container\">");
        html.append("        <div class=\"header\">");
        html.append("            <h1>📊 Панель управления</h1>");
        html.append("            <p>Добро пожаловать, " + user.getUsername() + "!</p>");
        html.append("        </div>");
        html.append("        ");
        html.append("        <div class=\"stats-grid\">");
        html.append("            <div class=\"stat-card\"><h3>150</h3><p>Всего пользователей</p></div>");
        html.append("            <div class=\"stat-card\"><h3>87</h3><p>Активных пользователей</p></div>");
        html.append("            <div class=\"stat-card\"><h3>2,450</h3><p>Сыгранных игр</p></div>");
        html.append("            <div class=\"stat-card\"><h3>1,567</h3><p>Средний счет</p></div>");
        html.append("        </div>");
        html.append("        ");
        html.append("        <div class=\"nav\">");
        html.append("            <a href='" + contextPath + "/welcome'>🏠 Главная</a>");
        html.append("            <a href='" + contextPath + "/game'>🎮 Игра</a>");
        html.append("            <a href='" + contextPath + "/logout'>🚪 Выйти</a>");
        html.append("        </div>");
        html.append("        ");
        html.append("        <div style=\"text-align: center; margin-top: 40px; padding: 20px; background: #f8f9fa; border-radius: 10px;\">");
        html.append("            <h3>📈 Статистика будет здесь</h3>");
        html.append("            <p>Графики и диаграммы появятся в следующем обновлении</p>");
        html.append("        </div>");
        html.append("    </div>");
        html.append("</body>");
        html.append("</html>");

        response.getWriter().println(html.toString());
    }
}