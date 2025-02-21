package com.sa1mone.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Цей ендпоінт доступний для всіх!";
    }

    @GetMapping("/private")
    public Map<String, Object> privateEndpoint(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "message", "Цей ендпоінт доступний тільки авторизованим користувачам!",
                "user", jwt.getClaims()
        );
    }
}