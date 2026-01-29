package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    @GetMapping("/dashboard")
    public String showDashboard(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);

        // Authentication check
        if (session == null || session.getAttribute("user") == null) {
            return "redirect:/auth";
        }

        User user = (User) session.getAttribute("user");

        // Add data to model
        model.addAttribute("user", user);
        model.addAttribute("contextPath", request.getContextPath());

        // Statistics (will be from database in the future)
        model.addAttribute("totalUsers", 150);
        model.addAttribute("activeUsers", 87);
        model.addAttribute("totalGames", 2450);
        model.addAttribute("averageScore", 1567);

        return "dashboard"; // dashboard.jsp
    }
}