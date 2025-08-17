package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/game")
public class GameServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login");
            return;
        }

        String step = request.getParameter("step");
        String choice = request.getParameter("choice");

        if (step == null) {
            // Start a new game
            session.setAttribute("gameState", "start");
            request.setAttribute("message", "Вы просыпаетесь в темном лесу. Перед вами две тропинки.");
            request.setAttribute("option1", "Пойти налево");
            request.setAttribute("option2", "Пойти направо");
        } else {
            // Game Logic Processing
            String gameState = (String) session.getAttribute("gameState");
            String nextStep = processGameStep(gameState, choice);
            session.setAttribute("gameState", nextStep);

            switch (nextStep) {
                case "left1":
                    request.setAttribute("message", "Вы нашли маленькую хижину. Зайти внутрь или идти дальше?");
                    request.setAttribute("option1", "Зайти в хижину");
                    request.setAttribute("option2", "Идти дальше");
                    break;
                case "right1":
                    request.setAttribute("message", "Вы видите реку. Переплыть или идти вдоль берега?");
                    request.setAttribute("option1", "Переплыть");
                    request.setAttribute("option2", "Идти вдоль берега");
                    break;
                case "win":
                    user.incrementGamesPlayed();
                    request.setAttribute("message", "Поздравляем! Вы нашли сокровище и победили!");
                    request.getRequestDispatcher("/WEB-INF/views/result.jsp").forward(request, response);
                    return;
                case "lose":
                    user.incrementGamesPlayed();
                    request.setAttribute("message", "О нет! Вы попали в ловушку и проиграли.");
                    request.getRequestDispatcher("/WEB-INF/views/result.jsp").forward(request, response);
                    return;
                default:
                    request.setAttribute("message", "Неверное состояние игры. Начинаем заново.");
                    session.setAttribute("gameState", "start");
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/game.jsp").forward(request, response);
    }

    private String processGameStep(String currentState, String choice) {
        if (currentState == null) return "start";

        switch (currentState) {
            case "start":
                return "1".equals(choice) ? "left1" : "right1";
            case "left1":
                return "1".equals(choice) ? "win" : "lose";
            case "right1":
                return "1".equals(choice) ? "lose" : "win";
            default:
                return "start";
        }
    }
}
