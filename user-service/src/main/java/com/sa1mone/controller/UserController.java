package com.sa1mone.controller;

import com.sa1mone.entity.User;
import com.sa1mone.request.UserUpdateRequest;
import com.sa1mone.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@RequestHeader(value = "X-Roles") String rolesHeader, @PathVariable UUID id) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin")) {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } else 
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader(value = "X-Roles") String rolesHeader) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin")) {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@RequestHeader(value = "X-Roles") String rolesHeader, @PathVariable UUID id, @Valid @RequestBody User user) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin")) {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@RequestHeader(value = "X-Roles") String rolesHeader, @PathVariable UUID id) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin")) {
            userService.deactivateUser(id);
            return ResponseEntity.noContent().build();
        } else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }


    @PutMapping("/me")
    public ResponseEntity<Map<String, Object>> updateAuthenticatedUser(@RequestBody UserUpdateRequest userUpdateRequest, Principal principal) {
        System.out.println(principal);
        String email = principal.getName();

        boolean isUpdated = userService.updateUserInfo(email, userUpdateRequest);

        if (!isUpdated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "User not found"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User information updated successfully"
        ));
    }
}