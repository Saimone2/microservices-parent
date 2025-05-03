package com.sa1mone.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRegistrationRequest {

    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Size(max = 255, message = "Password must not exceed 255 characters")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d).{6,255}$",
            message = "Password must contain at least one uppercase letter and one digit"
    )
    private String password;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 25, message = "The first name must be between 2 and 25 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 25, message = "The last name must be between 2 and 25 characters")
    private String lastName;

    @NotBlank(message = "A phone number is required")
    @Pattern(regexp = "^(\\+380|380|0)\\d{9}$", message = "Phone number must be valid")
    private String phoneNumber;

    @NotBlank(message = "Address required")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    private String role;
}