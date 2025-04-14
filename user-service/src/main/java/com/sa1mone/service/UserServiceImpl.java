package com.sa1mone.service;

import com.sa1mone.entity.User;
import com.sa1mone.messaging.UserPublisher;
import com.sa1mone.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already in use");
        }
        User savedUser = userRepository.save(user);
        userPublisher.publishUserUpdate("user.created",
                "User " + savedUser.getFirstName() + " " + savedUser.getLastName() + " created with email: " + savedUser.getEmail());
        return savedUser;
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Override
    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setAddress(updatedUser.getAddress());

        User savedUser = userRepository.save(existingUser);
        userPublisher.publishUserUpdate("user.updated",
                "User " + savedUser.getFirstName() + " " + savedUser.getLastName() + " updated successfully");
        return savedUser;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }
}