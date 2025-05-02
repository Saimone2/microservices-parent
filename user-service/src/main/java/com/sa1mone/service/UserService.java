package com.sa1mone.service;

import com.sa1mone.entity.User;
import com.sa1mone.request.UserUpdateRequest;
import com.sa1mone.response.UserResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserService {
    User saveUser(Map<String, Object> userData);
    User getUserById(UUID id);
    User updateUser(UUID id, User updatedUser);
    List<User> getAllUsers();
    void deactivateUser(UUID id);
    boolean updateLastLogin(String email, LocalDateTime lastLogin);
    void activateUser(UUID id);
    void updateUserByEmail(String email, UserUpdateRequest userUpdateRequest);
    User getUserByEmail(String email);
    boolean deactivateAuthenticatedUser(String email);
    UserResponse mapUserToResponse(User user);
}