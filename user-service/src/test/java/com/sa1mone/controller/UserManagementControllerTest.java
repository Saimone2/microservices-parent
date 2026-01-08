package com.sa1mone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sa1mone.entity.User;
import com.sa1mone.response.UserResponse;
import com.sa1mone.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserManagementController.class)
class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void updateLastLogin_shouldReturnSuccess() throws Exception {
        String email = "test@example.com";
        LocalDateTime lastLogin = LocalDateTime.now();

        when(userService.updateLastLogin(email, lastLogin)).thenReturn(true);

        Map<String, Object> request = Map.of(
                "email", email,
                "lastLogin", lastLogin.toString()
        );

        mockMvc.perform(post("/management/user/update-last-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User updated successfully"));

        verify(userService).updateLastLogin(email, lastLogin);
    }

    @Test
    void updateLastLogin_shouldReturnNotFound() throws Exception {
        String email = "missing@example.com";
        LocalDateTime lastLogin = LocalDateTime.now();

        when(userService.updateLastLogin(email, lastLogin)).thenReturn(false);

        Map<String, Object> request = Map.of(
                "email", email,
                "lastLogin", lastLogin.toString()
        );

        mockMvc.perform(post("/management/user/update-last-login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void saveUser_shouldReturnCreatedUser() throws Exception {
        User savedUser = new User();
        savedUser.setEmail("new@example.com");

        when(userService.saveUser(Map.of("email", "new@example.com"))).thenReturn(savedUser);

        Map<String, Object> request = Map.of("email", "new@example.com");

        mockMvc.perform(post("/management/user/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User saved successfully"))
                .andExpect(jsonPath("$.data.email").value("new@example.com"));
    }

    @Test
    void getUserByEmail_shouldReturnUserResponse() throws Exception {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        UserResponse response = new UserResponse();
        response.setEmail(email);

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(userService.mapUserToResponse(user)).thenReturn(response);

        mockMvc.perform(get("/management/user/find-by-email")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getUserByEmail_shouldReturnNotFound() throws Exception {
        String email = "missing@example.com";

        when(userService.getUserByEmail(email)).thenReturn(null);

        mockMvc.perform(get("/management/user/find-by-email")
                        .param("email", email))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found"));
    }
}