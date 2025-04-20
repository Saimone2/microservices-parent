package com.sa1mone.service;

import com.sa1mone.entity.User;
import com.sa1mone.request.UserUpdateRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {
    User saveUser(User user);
    User getUserById(Long id);
    User updateUser(Long id, User updatedUser);
    List<User> getAllUsers();
    void deactivateUser(Long id);
    boolean updateLastLogin(String email, LocalDateTime lastLogin);
    boolean updateUserInfo(String email, UserUpdateRequest userUpdateRequest);
}