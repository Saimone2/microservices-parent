package com.sa1mone.filling;

import com.sa1mone.entity.User;
import com.sa1mone.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TestDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    public TestDataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.save(new User("Alice", "Brown", "alice.brown@example.com", "+1234567890", "123 Main St, Springfield", true, LocalDateTime.now().minusDays(1), LocalDateTime.now().minusYears(1)));
            userRepository.save(new User("Bob", "Smith", "bob.smith@example.com", "+9876543210", "456 Park Ave, Sunnyvale", true, LocalDateTime.now().minusHours(5), LocalDateTime.now().minusYears(2)));
            userRepository.save(new User("Charlie", "Johnson", "charlie.johnson@example.com", "+1122334455", "789 Market St, Riverdale", true, LocalDateTime.now().minusMinutes(10), LocalDateTime.now().minusMonths(6)));
            userRepository.save(new User("Diana", "Wilson", "diana.wilson@example.com", "+2233445566", "321 Elm St, Oakland", true, LocalDateTime.now().minusDays(3), LocalDateTime.now().minusYears(3)));
            userRepository.save(new User("Edward", "Taylor", "edward.taylor@example.com", "+3344556677", "555 Pine St, Los Angeles", true, LocalDateTime.now().minusWeeks(1), LocalDateTime.now().minusMonths(9)));
            System.out.println("Test data loaded successfully!");
        }
    }
}