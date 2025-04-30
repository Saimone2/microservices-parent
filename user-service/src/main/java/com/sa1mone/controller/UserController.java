package com.sa1mone.controller;

import com.sa1mone.entity.User;
import com.sa1mone.request.UserUpdateRequest;
import com.sa1mone.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/me/update")
    public ResponseEntity<Map<String, Object>> updateAuthenticatedUser(@RequestHeader("X-User-Email") String email, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        userService.updateUserByEmail(email, userUpdateRequest);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "User information updated successfully"
        ));
    }

    @PostMapping("/me/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateAuthenticatedUser(@RequestHeader("X-User-Email") String email) {
        if(userService.deactivateAuthenticatedUser(email)) {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "User deactivated successfully"
            ));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "success", false,
                "message", "User has already been deactivated"
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getAuthenticatedUser(@RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }
}