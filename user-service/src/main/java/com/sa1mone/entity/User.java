package com.sa1mone.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    private Boolean isActive;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;

    public User(String firstName, String lastName, String email, String phoneNumber, String address, Boolean isActive, LocalDateTime createdAt, LocalDateTime lastLogin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }
}