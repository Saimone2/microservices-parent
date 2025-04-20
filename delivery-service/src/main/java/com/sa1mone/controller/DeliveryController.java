package com.sa1mone.controller;

import com.sa1mone.entity.Delivery;
import com.sa1mone.enums.DeliveryStatus;
import com.sa1mone.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping
    public Delivery createDelivery(@RequestParam UUID orderId, @RequestParam String address) {
        return deliveryService.createDelivery(orderId, address);
    }

    @PutMapping("/{deliveryId}")
    public Delivery updateDeliveryStatus(@PathVariable UUID deliveryId, @RequestParam DeliveryStatus status) {
        return deliveryService.updateDeliveryStatus(deliveryId, status);
    }

    @GetMapping("/order/{orderId}")
    public Delivery getDeliveryByOrderId(@PathVariable UUID orderId) {
        return deliveryService.getDeliveryByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found for order"));
    }
}