package com.sa1mone.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @PostMapping("/login")
    public String login(@RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient authorizedClient) {
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        return "Access Token: " + accessToken.getTokenValue();
    }
}
