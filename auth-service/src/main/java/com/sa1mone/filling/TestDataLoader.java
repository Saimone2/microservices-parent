package com.sa1mone.filling;

import com.sa1mone.requests.UserRegistrationRequest;
import com.sa1mone.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestDataLoader implements CommandLineRunner {

    private final AuthService authService;

    public TestDataLoader(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void run(String... args) {
        // authService.deleteAllUsers();
        if (authService.isUserTableEmpty()) {
            List<UserRegistrationRequest> testUsers = List.of(
                    new UserRegistrationRequest("admin@gmail.com", "securepass", "Andrew", "Khrushch", "+380967933134", "10 Kyivska st.", "admin"),
                    new UserRegistrationRequest("product_manager1@gmail.com", "securepass", "Maria", "Petrova", "+380967933135", "11 Kyivska st.", "product_manager"),
                    new UserRegistrationRequest("product_manager2@gmail.com", "securepass", "John", "Doe", "+380967933136", "12 Kyivska st.", "product_manager"),
                    new UserRegistrationRequest("user1@gmail.com", "securepass", "Alice", "Johnson", "+380967933137", "13 Kyivska st.", "user"),
                    new UserRegistrationRequest("user2@gmail.com", "securepass", "Bob", "Smith", "+380967933138", "14 Kyivska st.", "user")
            );
            testUsers.forEach(this::registerTestUser);
        }
    }

    private void registerTestUser(UserRegistrationRequest request) {
        authService.registerUser(
                authService.buildKeycloakRequest(request),
                authService.buildUserServiceRequest(request)
        );
    }
}