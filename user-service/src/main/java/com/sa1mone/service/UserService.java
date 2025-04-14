package com.sa1mone.service;

import com.sa1mone.entity.User;

import java.util.List;

public interface UserService {
    User createUser(User user);
    User getUserById(Long id);
    User updateUser(Long id, User updatedUser);
    List<User> getAllUsers();
    void deactivateUser(Long id);
}