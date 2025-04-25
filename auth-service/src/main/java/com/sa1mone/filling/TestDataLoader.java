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
        if (authService.isUserTableEmpty()) {
            List<UserRegistrationRequest> testUsers = List.of(
                    new UserRegistrationRequest("test1@gmail.com", "securepass1", "Andrew", "Khrushch", "+380967933134", "10 Kyivska st."),
                    new UserRegistrationRequest("test2@gmail.com", "securepass2", "Maria", "Petrova", "+380967933135", "11 Kyivska st."),
                    new UserRegistrationRequest("test3@gmail.com", "securepass3", "John", "Doe", "+380967933136", "12 Kyivska st."),
                    new UserRegistrationRequest("test4@gmail.com", "securepass4", "Alice", "Johnson", "+380967933137", "13 Kyivska st."),
                    new UserRegistrationRequest("test5@gmail.com", "securepass5", "Bob", "Smith", "+380967933138", "14 Kyivska st.")
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