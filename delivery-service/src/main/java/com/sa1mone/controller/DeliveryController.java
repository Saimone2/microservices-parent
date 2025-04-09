package com.sa1mone.controller;

import com.sa1mone.entity.Delivery;
import com.sa1mone.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @PostMapping
    public Delivery createDelivery(@RequestParam Long orderId, @RequestParam String address) {
        return deliveryService.createDelivery(orderId, address);
    }

    @PutMapping("/{deliveryId}")
    public Delivery updateDeliveryStatus(@PathVariable Long deliveryId, @RequestParam String status) {
        return deliveryService.updateDeliveryStatus(deliveryId, status);
    }

    @GetMapping("/order/{orderId}")
    public Delivery getDeliveryByOrderId(@PathVariable Long orderId) {
        return deliveryService.getDeliveryByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found for order"));
    }
}