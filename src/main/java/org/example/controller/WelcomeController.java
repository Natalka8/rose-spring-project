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
public class WelcomeController {

    @GetMapping("/welcome")
    public String showWelcome(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);

        // Authentication check
        if (session == null || session.getAttribute("user") == null) {
            return "redirect:/auth";
        }

        User user = (User) session.getAttribute("user");

        // Add data to model (instead of request.setAttribute)
        model.addAttribute("user", user);
        model.addAttribute("contextPath", request.getContextPath());

        return "welcome"; // Will look for welcome.jsp
    }
}