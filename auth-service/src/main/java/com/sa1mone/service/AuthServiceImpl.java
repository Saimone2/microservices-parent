package com.sa1mone.service;

import com.sa1mone.config.KeycloakProperties;
import com.sa1mone.requests.UserRegistrationRequest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@Service
public class AuthServiceImpl implements AuthService {
    private final RestTemplate restTemplate;
    private final KeycloakProperties properties;

    public AuthServiceImpl(RestTemplateBuilder restTemplateBuilder, KeycloakProperties properties) {
        this.restTemplate = restTemplateBuilder.build();
        this.properties = properties;
    }

    @Override
    public void registerUser(Map<String, Object> userDataKeycloak, Map<String, Object> userDataService) {
        validateUserData(userDataKeycloak);

        String url = buildUrl("admin/realms/" + properties.getRealm() + "/users");
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    url,
                    buildHttpEntity(userDataKeycloak, getAdminAccessToken(), MediaType.APPLICATION_JSON),
                    String.class
            );

            String userId = extractUserIdFromResponse(response);

            assignRoleToUser(userId, (String) userDataService.get("role"));
            saveUserToUserService(userDataService);
            
        } catch (HttpClientErrorException e) {
            handleHttpError(e, "Error during user registration");
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred during registration: " + e.getMessage(), e);
        }
    }

    private void assignRoleToUser(String userId, String roleName) {
        String roleUrl = buildUrl("admin/realms/" + properties.getRealm() + "/roles/" + roleName);
        String mappingUrl = buildUrl("admin/realms/" + properties.getRealm() + "/users/" + userId + "/role-mappings/realm");

        ResponseEntity<Map> roleResponse = restTemplate.exchange(
                roleUrl,
                HttpMethod.GET,
                buildHttpEntity(null, getAdminAccessToken(), MediaType.APPLICATION_JSON),
                Map.class
        );

        String roleId = Objects.requireNonNull(roleResponse.getBody()).get("id").toString();
        Map<String, Object> roleData = Map.of(
                "id", roleId,
                "name", roleName
        );

        restTemplate.postForEntity(
                mappingUrl,
                buildHttpEntity(List.of(roleData), getAdminAccessToken(), MediaType.APPLICATION_JSON),
                Void.class
        );
    }

    private String extractUserIdFromResponse(ResponseEntity<String> response) {
        String locationHeader = Objects.requireNonNull(response.getHeaders().getLocation()).toString();
        return locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
    }

    private void saveUserToUserService(Map<String, Object> userData) {
        String userServiceUrl = "http://user-service:8081/management/user/save";
        try {
            restTemplate.postForEntity(
                    userServiceUrl,
                    buildHttpEntity(userData, null, MediaType.APPLICATION_JSON),
                    String.class
            );
        } catch (HttpClientErrorException e) {
            handleHttpError(e, "Error while saving user to user-service");
        }
    }

    @Override
    public Map<String, Object> loginUser(String email, String password) {
        validateLoginData(email, password);
        String url = buildUrl("realms/" + properties.getRealm() + "/protocol/openid-connect/token");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", properties.getClientId());
        body.add("client_secret", properties.getClientSecret());
        body.add("username", email);
        body.add("password", password);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    url,
                    buildHttpEntity(body, null, MediaType.APPLICATION_FORM_URLENCODED),
                    Map.class
            );
            updateLastLogin(email);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            handleHttpError(e, "Error during user login");
            return null;
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred during login: " + e.getMessage(), e);
        }
    }

    public String getAdminAccessToken() {
        String url = buildUrl("realms/" + properties.getRealm() + "/protocol/openid-connect/token");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", properties.getClientId());
        body.add("client_secret", properties.getClientSecret());
        body.add("grant_type", "client_credentials");

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    url,
                    buildHttpEntity(body, null, MediaType.APPLICATION_FORM_URLENCODED),
                    Map.class
            );
            return (String) Objects.requireNonNull(response.getBody()).get("access_token");
        } catch (HttpClientErrorException e) {
            handleHttpError(e, "Error during token retrieval");
            return null;
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred during token retrieval: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deactivateUser(String email) {
        String url = buildUrl("admin/realms/" + properties.getRealm() + "/users");
        List<Map<String, Object>> users;
        try {
            ResponseEntity<List> response = restTemplate.exchange(
                    url + "?email=" + email,
                    HttpMethod.GET,
                    buildHttpEntity(null, getAdminAccessToken(), MediaType.APPLICATION_JSON),
                    List.class
            );
            users = response.getBody();
        } catch (HttpClientErrorException e) {
            handleHttpError(e, "Error while fetching user from Keycloak");
            return false;
        }

        if (users == null || users.isEmpty()) {
            return false;
        }

        String userId = (String) users.get(0).get("id");

        try {
            Map<String, Object> updateBody = Map.of("enabled", false);
            restTemplate.put(
                    url + "/" + userId,
                    buildHttpEntity(updateBody, getAdminAccessToken(), MediaType.APPLICATION_JSON)
            );
            return true;
        } catch (HttpClientErrorException e) {
            handleHttpError(e, "Error while deactivating user in Keycloak");
            return false;
        }
    }

    private String buildUrl(String path) {
        return properties.getAuthServerUrl() + "/" + path;
    }

    private <T> HttpEntity<T> buildHttpEntity(T body, String bearerToken, MediaType contentType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(contentType);
        if (bearerToken != null) {
            headers.setBearerAuth(bearerToken);
        }
        return new HttpEntity<>(body, headers);
    }

    private void validateUserData(Map<String, Object> userData) {
        if (!userData.containsKey("username") || userData.get("username").toString().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (!userData.containsKey("email") || userData.get("email").toString().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!userData.containsKey("credentials")) {
            throw new IllegalArgumentException("Credentials are required");
        }
    }

    private void validateLoginData(String email, String password) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
    }

    private void handleHttpError(HttpClientErrorException e, String contextMessage) {
        switch (e.getStatusCode()) {
            case CONFLICT -> throw new ResponseStatusException(HttpStatus.CONFLICT, "Conflict: User exists with same email");
            case UNAUTHORIZED -> throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
            case FORBIDDEN -> throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient permissions: " + e.getResponseBodyAsString());
            case BAD_REQUEST -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad request: " + e.getResponseBodyAsString());
            default -> throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, contextMessage + ": " + e.getResponseBodyAsString());
        }
    }

    @Override
    public Map<String, Object> buildKeycloakRequest(UserRegistrationRequest request) {
        return Map.of(
                "username", request.getEmail(),
                "email", request.getEmail(),
                "enabled", true,
                "emailVerified", true,
                "firstName", request.getFirstName(),
                "lastName", request.getLastName(),
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", request.getPassword(),
                        "temporary", false
                ))
        );
    }

    @Override
    public Map<String, Object> buildUserServiceRequest(UserRegistrationRequest request) {
        return Map.of(
                "email", request.getEmail(),
                "enabled", true,
                "firstName", request.getFirstName(),
                "lastName", request.getLastName(),
                "phoneNumber", request.getPhoneNumber(),
                "address", request.getAddress(),
                "role", request.getRole(),
                "isActive", true
        );
    }

    @Override
    public boolean updateUserInKeycloak(Map<String, Object> request) {
        String email = (String) request.get("email");
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required for updating user information");
        }
        request.remove("email");

        String url = buildUrl("admin/realms/" + properties.getRealm() + "/users");
        List<Map<String, Object>> users;
        try {
            ResponseEntity<List> response = restTemplate.exchange(
                    url + "?email=" + email,
                    HttpMethod.GET,
                    buildHttpEntity(request, getAdminAccessToken(), MediaType.APPLICATION_JSON),
                    List.class
            );
            users = response.getBody();
        } catch (HttpClientErrorException e) {
            handleHttpError(e, "Error while fetching user from Keycloak");
            return false;
        }

        if (users == null || users.isEmpty()) {
            return false;
        }

        String userId = (String) users.get(0).get("id");

        try {
            restTemplate.put(
                    url + "/" + userId,
                    buildHttpEntity(request, getAdminAccessToken(), MediaType.APPLICATION_JSON)
            );
            return true;
        } catch (HttpClientErrorException e) {
            handleHttpError(e, "Error while updating user in Keycloak");
            return false;
        }
    }

    @Override
    public boolean isUserTableEmpty() {
        String url = buildUrl("admin/realms/" + properties.getRealm() + "/users");

        try {
            ResponseEntity<List> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    buildHttpEntity(null, getAdminAccessToken(), MediaType.APPLICATION_JSON),
                    List.class
            );

            List<?> users = response.getBody();
            return users == null || users.isEmpty();
        } catch (HttpClientErrorException e) {
            handleHttpError(e, "Error checking Keycloak user table");
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while checking Keycloak users: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean activateUser(String email) {
        String url = buildUrl("admin/realms/" + properties.getRealm() + "/users");
        List<Map<String, Object>> users;
        try {
            ResponseEntity<List> response = restTemplate.exchange(
                    url + "?email=" + email,
                    HttpMethod.GET,
                    buildHttpEntity(null, getAdminAccessToken(), MediaType.APPLICATION_JSON),
                    List.class
            );
            users = response.getBody();
        } catch (HttpClientErrorException e) {
            handleHttpError(e, "Error while fetching user from Keycloak");
            return false;
        }

        if (users == null || users.isEmpty()) {
            return false;
        }

        String userId = (String) users.get(0).get("id");

        try {
            Map<String, Object> updateBody = Map.of("enabled", true);
            restTemplate.put(
                    url + "/" + userId,
                    buildHttpEntity(updateBody, getAdminAccessToken(), MediaType.APPLICATION_JSON)
            );
            return true;
        } catch (HttpClientErrorException e) {
            handleHttpError(e, "Error while activating user in Keycloak");
            return false;
        }
    }

    private void updateLastLogin(String email) {
        String userServiceUrl = "http://user-service:8081/management/user/update-last-login";

        Map<String, Object> requestBody = Map.of(
                "email", email,
                "lastLogin", LocalDateTime.now()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(userServiceUrl, entity, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to update last login");
            }
        } catch (HttpClientErrorException e) {
            handleHttpError(e, "Error while updating last login");
        }
    }
}