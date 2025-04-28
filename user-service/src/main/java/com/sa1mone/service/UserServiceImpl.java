package com.sa1mone.service;

import com.sa1mone.entity.User;
import com.sa1mone.messaging.UserPublisher;
import com.sa1mone.repo.UserRepository;
import com.sa1mone.request.UserUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserPublisher userPublisher;
    private final RestTemplate restTemplate;

    @Autowired
    public UserServiceImpl(RestTemplateBuilder restTemplateBuilder, UserRepository userRepository, UserPublisher userPublisher) {
        this.restTemplate = restTemplateBuilder.build();
        this.userRepository = userRepository;
        this.userPublisher = userPublisher;
    }

    @Override
    public User saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already in use");
        }
        LocalDateTime now = LocalDateTime.now();

        user.setIsActive(true);
        user.setLastLogin(now);
        user.setCreatedAt(now);

        User savedUser = userRepository.save(user);
        userPublisher.publishUserUpdate("user.created",
                "User " + savedUser.getFirstName() + " " + savedUser.getLastName() + " created with email: " + savedUser.getEmail());
        return savedUser;
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Override
    public User updateUser(UUID id, User updatedUser) {
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
    public void deactivateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setIsActive(false);
        deactivateUserInKeycloak(user.getEmail());
        userRepository.save(user);
    }

    @Override
    public boolean updateLastLogin(String email, LocalDateTime lastLogin) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        }
        user.setLastLogin(lastLogin);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean updateUserInfo(String email, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        }
        if (userUpdateRequest.getFirstName() != null) {
            user.setFirstName(userUpdateRequest.getFirstName());
        }
        if (userUpdateRequest.getLastName() != null) {
            user.setLastName(userUpdateRequest.getLastName());
        }
        if (userUpdateRequest.getPhoneNumber() != null) {
            user.setPhoneNumber(userUpdateRequest.getPhoneNumber());
        }
        if (userUpdateRequest.getAddress() != null) {
            user.setAddress(userUpdateRequest.getAddress());
        }

        updateUserInKeycloak(email);
        userRepository.save(user);
        return true;
    }

    private void deactivateUserInKeycloak(String email) {
        String authServiceUrl = "http://auth-service:8087/management/auth/deactivate-user";

        Map<String, Object> requestBody = Map.of(
                "email", email
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    authServiceUrl,
                    new HttpEntity<>(requestBody, headers),
                    Map.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to deactivate user in Keycloak");
            }

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error while deactivating user in Keycloak: " + e.getMessage(), e);
        }
    }

    private void updateUserInKeycloak(String email) {
        String authServiceUrl = "http://auth-service:8087/management/auth/update-user";

        Map<String, Object> requestBody = Map.of(
                "email", email
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    authServiceUrl,
                    new HttpEntity<>(requestBody, headers),
                    Map.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to update user in Keycloak");
            }

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Error while updating user in Keycloak: " + e.getMessage(), e);
        }
    }
}