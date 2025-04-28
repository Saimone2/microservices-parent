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

    @NotBlank(message = "Email cannot be blank")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Size(max = 255, message = "Password must not exceed 255 characters")
    private String password;

    @NotBlank(message = "The first name cannot be blank")
    @Size(min = 2, max = 25, message = "The first name must be between 2 and 25 characters")
    private String firstName;

    @NotBlank(message = "The last name cannot be blank")
    @Size(min = 2, max = 25, message = "The last name must be between 2 and 25 characters")
    private String lastName;

    @Pattern(regexp = "^(\\+380|380|0)\\d{9}$", message = "Phone number must be valid")
    private String phoneNumber;

    @NotBlank(message = "Address cannot be blank")
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;

    private String role;
}