package com.sa1mone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sa1mone.entity.User;
import com.sa1mone.request.UserUpdateRequest;
import com.sa1mone.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void updateAuthenticatedUser_shouldReturnSuccess() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setFirstName("Andriy");
        request.setLastName("Test");

        mockMvc.perform(post("/user/me/update")
                        .header("X-User-Email", "test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User information updated successfully"));

        verify(userService).updateUserByEmail("test@example.com", request);
    }

    @Test
    void deactivateAuthenticatedUser_shouldReturnSuccess() throws Exception {
        when(userService.deactivateAuthenticatedUser("test@example.com")).thenReturn(true);

        mockMvc.perform(post("/user/me/deactivate")
                        .header("X-User-Email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User deactivated successfully"));
    }

    @Test
    void deactivateAuthenticatedUser_shouldReturnConflict() throws Exception {
        when(userService.deactivateAuthenticatedUser("test@example.com")).thenReturn(false);

        mockMvc.perform(post("/user/me/deactivate")
                        .header("X-User-Email", "test@example.com"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User has already been deactivated"));
    }

    @Test
    void getAuthenticatedUser_shouldReturnUser() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("Andriy");
        user.setLastName("Test");

        when(userService.getUserByEmail("test@example.com")).thenReturn(user);

        mockMvc.perform(get("/user/me")
                        .header("X-User-Email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Andriy"))
                .andExpect(jsonPath("$.lastName").value("Test"));
    }
}