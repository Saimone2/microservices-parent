package com.sa1mone.controller;

import com.sa1mone.entity.User;
import com.sa1mone.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserManagementController {

    private final UserService userService;

    @Autowired
    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/update-last-login")
    public ResponseEntity<Map<String, Object>> updateLastLogin(@RequestBody Map<String, Object> request) {
        String email = (String) request.get("email");
        LocalDateTime lastLogin = LocalDateTime.parse((String) request.get("lastLogin"));

        boolean isUpdated = userService.updateLastLogin(email, lastLogin);

        if (!isUpdated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "User not found"
            ));
        }
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User lastLogin updated successfully"
        ));
    }

    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveUser(@Valid @RequestBody User user) {
        User savedUser = userService.saveUser(user);

        Map<String, Object> response = Map.of(
                "success", true,
                "message", "User saved successfully",
                "data", savedUser
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
