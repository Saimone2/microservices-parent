package com.sa1mone.controller;

import com.sa1mone.requests.UserLoginRequest;
import com.sa1mone.requests.UserRegistrationRequest;
import com.sa1mone.service.AuthServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthServiceImpl authService;

    public AuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        request.setRole("user");

        authService.registerUser(
                authService.buildKeycloakRequest(request),
                authService.buildUserServiceRequest(request)
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "User registered successfully"
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@Valid @RequestBody UserLoginRequest request) {
        Map<String, Object> response = authService.loginUser(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }
}