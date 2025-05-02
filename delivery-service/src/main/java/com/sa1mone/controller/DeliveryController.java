package com.sa1mone.controller;

import com.sa1mone.response.DeliveryResponse;
import com.sa1mone.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/me")
    public ResponseEntity<List<DeliveryResponse>> getAuthenticatedUserDeliveries(@RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(deliveryService.getUserDeliveriesByEmail(email));
    }
}