package com.sa1mone.service;

import com.sa1mone.requests.UserRegistrationRequest;

import java.util.Map;

public interface AuthService {
    void registerUser(Map<String, Object> userDataKeycloak, Map<String, Object> userDataService);
    void deleteAllUsers();
    Map<String, Object> loginUser(String email, String password);
    String getAdminAccessToken();
    boolean deactivateUser(String email);
    Map<String, Object> buildKeycloakRequest(UserRegistrationRequest request);
    Map<String, Object> buildUserServiceRequest(UserRegistrationRequest request);
    boolean updateUserInKeycloak(Map<String, Object> request);
    boolean isUserTableEmpty();
    boolean activateUser(String email);
}