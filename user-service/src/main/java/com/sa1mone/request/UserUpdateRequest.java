package com.sa1mone.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
public class UserUpdateRequest {
    @Size(min = 2, max = 25, message = "The first name must be between 2 and 25 characters")
    private String firstName;

    @Size(min = 2, max = 25, message = "The last name must be between 2 and 25 characters")
    private String lastName;

    @Pattern(regexp = "^(\\+380|380|0)\\d{9}$", message = "Phone number must be valid")
    private String phoneNumber;

    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;
}