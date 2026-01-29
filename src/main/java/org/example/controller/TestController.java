package org.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Это публичный эндпоинт";
    }

    @GetMapping("/private")
    public String privateEndpoint() {
        return "Это приватный эндпоинт";
    }
}