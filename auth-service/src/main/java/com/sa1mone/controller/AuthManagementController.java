package com.sa1mone.controller;


import com.sa1mone.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/management/auth")
public class AuthManagementController {

    private final AuthService authService;

    public AuthManagementController(AuthService authService) {
        this.authService = authService;
    }

    @PutMapping("/update-user")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody Map<String, Object> request) {
        boolean isUpdated = authService.updateUserInKeycloak(request);

        if (!isUpdated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "User not found or update failed"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User information updated successfully"
        ));
    }

    @PostMapping("/deactivate-user")
    public ResponseEntity<Map<String, Object>> deactivateUser(@RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");

        boolean isDeactivated = authService.deactivateUser(email);

        if (!isDeactivated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "User not found or already inactive in Keycloak"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User deactivated successfully"
        ));
    }
}