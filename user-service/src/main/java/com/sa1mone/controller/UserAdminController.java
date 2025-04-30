package com.sa1mone.controller;

import com.sa1mone.entity.User;
import com.sa1mone.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserAdminController {
    private final UserService userService;

    @Autowired
    public UserAdminController(UserService userService) {
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
            return ResponseEntity.ok().build();
        } else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@RequestHeader(value = "X-Roles") String rolesHeader, @PathVariable UUID id) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin")) {
            userService.activateUser(id);
            return ResponseEntity.ok().build();
        } else
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
