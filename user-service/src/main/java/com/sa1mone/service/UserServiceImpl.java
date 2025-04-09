package com.sa1mone.service;

import com.sa1mone.entity.User;
import com.sa1mone.messaging.UserPublisher;
import com.sa1mone.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserPublisher userPublisher;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserPublisher userPublisher) {
        this.userRepository = userRepository;
        this.userPublisher = userPublisher;
    }

    @Override
    public User saveUser(User user) {
        User savedUser = userRepository.save(user);

        // Публікація повідомлення після збереження користувача
        userPublisher.publishUserUpdate("user.updated", "User " + savedUser.getName() + " updated!");
        return savedUser;
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.orElseThrow(() -> new RuntimeException("User not found"));
    }
}