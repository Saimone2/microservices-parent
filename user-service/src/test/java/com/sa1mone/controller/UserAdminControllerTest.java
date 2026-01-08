package com.sa1mone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sa1mone.entity.User;
import com.sa1mone.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserAdminController.class)
@ImportAutoConfiguration(exclude = {
        org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration.class
})
class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getUserById_shouldReturnUser_whenAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setEmail("admin@test.com");

        when(userService.getUserById(id)).thenReturn(user);

        mockMvc.perform(get("/user/{id}", id)
                        .header("X-Roles", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@test.com"));
    }

    @Test
    void getUserById_shouldReturnForbidden_whenNotAdmin() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(get("/user/{id}", id)
                        .header("X-Roles", "user"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_shouldReturnList_whenAdmin() throws Exception {
        User u1 = new User(); u1.setEmail("u1@test.com");
        User u2 = new User(); u2.setEmail("u2@test.com");

        when(userService.getAllUsers()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/user/all")
                        .header("X-Roles", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("u1@test.com"))
                .andExpect(jsonPath("$[1].email").value("u2@test.com"));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser_whenAdmin() throws Exception {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setFirstName("Andriy");
        user.setLastName("Test");
        user.setEmail("old@test.com");
        user.setAddress("Kyiv");

        User updated = new User();
        updated.setFirstName("Andriy");
        updated.setLastName("Test");
        updated.setEmail("new@test.com");
        updated.setAddress("Kyiv");

        when(userService.updateUser(id, user)).thenReturn(updated);

        mockMvc.perform(put("/user/{id}", id)
                        .header("X-Roles", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@test.com"));
    }

    @Test
    void deactivateUser_shouldReturnOk_whenAdmin() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/user/{id}/deactivate", id)
                        .header("X-Roles", "admin"))
                .andExpect(status().isOk());

        verify(userService).deactivateUser(id);
    }

    @Test
    void activateUser_shouldReturnOk_whenAdmin() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/user/{id}/activate", id)
                        .header("X-Roles", "admin"))
                .andExpect(status().isOk());

        verify(userService).activateUser(id);
    }

    @Test
    void anyAdminEndpoint_shouldReturnForbidden_whenNotAdmin() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/user/{id}/activate", id)
                        .header("X-Roles", "user"))
                .andExpect(status().isForbidden());
    }
}