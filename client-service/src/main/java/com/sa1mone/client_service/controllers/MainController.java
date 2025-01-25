package com.sa1mone.client_service.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @GetMapping("/example")
    public String example() {
        return "Hello from Service Example";
    }
}