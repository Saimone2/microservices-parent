package com.sa1mone.service;

import com.sa1mone.entity.User;
import com.sa1mone.repo.UserRepository;
import com.sa1mone.messaging.UserPublisher;
import com.sa1mone.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPublisher userPublisher;

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(restTemplateBuilder.build()).thenReturn(mock(org.springframework.web.client.RestTemplate.class));
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        userService = new UserServiceImpl(restTemplateBuilder, userRepository, userPublisher);
    }

    @Test
    void saveUser_shouldSaveAndPublish_whenEmailNotExists() {
        Map<String, Object> data = Map.of(
                "firstName", "Andriy",
                "lastName", "Test",
                "email", "test@example.com",
                "role", "USER"
        );

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        User saved = new User();
        saved.setEmail("test@example.com");
        saved.setFirstName("Andriy");
        saved.setLastName("Test");

        when(userRepository.save(any(User.class))).thenReturn(saved);

        User result = userService.saveUser(data);

        assertEquals("test@example.com", result.getEmail());
        verify(userPublisher).publishUserUpdate(eq("user.created"), contains("created with email"));
    }

    @Test
    void saveUser_shouldThrowConflict_whenEmailExists() {
        Map<String, Object> data = Map.of("email", "duplicate@example.com");
        when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> userService.saveUser(data));
    }

    @Test
    void getUserById_shouldReturnUser_whenExists() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setEmail("test@example.com");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User result = userService.getUserById(id);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getUserById_shouldThrowNotFound_whenMissing() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.getUserById(id));
    }

    @Test
    void updateUser_shouldUpdateAndPublish() {
        UUID id = UUID.randomUUID();
        User existing = new User();
        existing.setId(id);
        existing.setFirstName("Old");
        existing.setLastName("Name");

        User updated = new User();
        updated.setFirstName("New");
        updated.setLastName("Name");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenReturn(updated);

        User result = userService.updateUser(id, updated);

        assertEquals("New", result.getFirstName());
        verify(userPublisher).publishUserUpdate(eq("user.updated"), contains("updated successfully"));
    }

    @Test
    void deactivateUser_shouldSetInactive_whenActive() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setEmail("test@example.com");
        user.setIsActive(true);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(restTemplate.postForEntity(anyString(), any(), eq(Map.class)))
                .thenReturn(ResponseEntity.ok(Map.of("success", true)));

        userService.deactivateUser(id);

        assertFalse(user.getIsActive());
        verify(userRepository).save(user);
    }

    @Test
    void deactivateUser_shouldThrowConflict_whenAlreadyInactive() {
        UUID id = UUID.randomUUID();
        User user = new User();
        user.setId(id);
        user.setIsActive(false);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        assertThrows(ResponseStatusException.class, () -> userService.deactivateUser(id));
    }

    @Test
    void updateLastLogin_shouldReturnTrue_whenUserExists() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        boolean result = userService.updateLastLogin("test@example.com", LocalDateTime.now());

        assertTrue(result);
        verify(userRepository).save(user);
    }

    @Test
    void updateLastLogin_shouldReturnFalse_whenUserMissing() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(null);

        boolean result = userService.updateLastLogin("missing@example.com", LocalDateTime.now());

        assertFalse(result);
    }

    @Test
    void mapUserToResponse_shouldMapCorrectly() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setFirstName("Andriy");
        user.setLastName("Test");

        UserResponse response = userService.mapUserToResponse(user);

        assertEquals("test@example.com", response.getEmail());
        assertEquals("Andriy", response.getFirstName());
    }
}