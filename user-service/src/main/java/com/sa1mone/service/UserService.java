package com.sa1mone.service;

import com.sa1mone.entity.User;
import com.sa1mone.request.UserUpdateRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UserService {
    User saveUser(User user);
    User getUserById(UUID id);
    User updateUser(UUID id, User updatedUser);
    List<User> getAllUsers();
    void deactivateUser(UUID id);
    boolean updateLastLogin(String email, LocalDateTime lastLogin);
    void activateUser(UUID id);
    void updateUserByEmail(String email, UserUpdateRequest userUpdateRequest);
    User getUserByEmail(String email);
    boolean deactivateAuthenticatedUser(String email);
}