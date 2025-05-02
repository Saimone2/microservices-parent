package com.sa1mone.controller;

import com.sa1mone.request.DeliveryRequest;
import com.sa1mone.service.DeliveryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/management/delivery")
public class DeliveryManagementController {

    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryManagementController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createDelivery(@Valid @RequestBody DeliveryRequest request) {
        UUID deliveryId = deliveryService.createDelivery(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Delivery created successfully",
                "deliveryId", deliveryId
        ));
    }
}