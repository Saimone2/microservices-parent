package com.sa1mone.service;

import com.sa1mone.entity.User;

public interface UserService {
    User saveUser(User user);
    User getUserById(Long id);
}