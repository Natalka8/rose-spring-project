package org.example;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/stats")
public class StatsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("=== StatsServlet called ===");
        System.out.println("Request URL: " + request.getRequestURL());
        System.out.println("Context Path: " + request.getContextPath());

        // Простые тестовые данные
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head><title>Статистика</title></head>");
        out.println("<body>");
        out.println("<h1>📊 Статистика работает!</h1>");
        out.println("<p>Сервлет StatsServlet успешно загружен</p>");
        out.println("<p>Всего игроков: 5</p>");
        out.println("<a href='welcome'>Назад в игру</a>");
        out.println("</body>");
        out.println("</html>");
    }
}