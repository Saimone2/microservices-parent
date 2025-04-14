package com.sa1mone.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequest {
    private String email;
    private String password;
}