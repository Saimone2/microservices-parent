package com.sa1mone.controller;

import com.sa1mone.entity.Delivery;
import com.sa1mone.enums.DeliveryStatus;
import com.sa1mone.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/delivery")
public class DeliveryAdminController {

    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryAdminController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/{deliveryId}")
    public ResponseEntity<Delivery> getDeliveryById(@RequestHeader(value = "X-Roles") String rolesHeader, @PathVariable UUID deliveryId) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin") || roles.contains("product_manager")) {
            return ResponseEntity.ok(deliveryService.getDeliveryById(deliveryId));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Delivery>> getAllDeliveries(@RequestHeader(value = "X-Roles") String rolesHeader) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin") || roles.contains("product_manager")) {
            return ResponseEntity.ok(deliveryService.getAllDeliveries());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/{orderId}/status")
    public ResponseEntity<DeliveryStatus> getDeliveryStatus(@RequestHeader(value = "X-Roles") String rolesHeader, @PathVariable UUID orderId) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin") || roles.contains("product_manager")) {
            return ResponseEntity.ok(deliveryService.getDeliveryStatus(orderId));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}