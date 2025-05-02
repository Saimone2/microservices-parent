package com.sa1mone.response;

import lombok.Data;

import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;
    private String email;
    private String address;
    private String firstName;
    private String lastName;
}