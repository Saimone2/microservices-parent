package com.sa1mone.entity;

import com.sa1mone.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "The first name cannot be blank")
    @Size(min = 2, max = 25, message = "The last name must be between 2 and 25 characters")
    private String firstName;

    @NotBlank(message = "The last name cannot be blank")
    @Size(min = 2, max = 25, message = "The last name must be between 2 and 25 characters")
    private String lastName;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    private String email;

    @Pattern(regexp = "^[+]?\\d{10,15}$", message = "Phone number must be valid")
    private String phoneNumber;

    @NotBlank(message = "Address cannot be blank")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    @NotBlank
    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean isActive;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
}