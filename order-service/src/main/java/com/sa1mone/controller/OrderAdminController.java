package com.sa1mone.controller;

import com.sa1mone.entity.Order;
import com.sa1mone.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderAdminController {

    private final OrderService orderService;

    @Autowired
    public OrderAdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/from-user")
    public ResponseEntity<List<Order>> getUserOrdersById(@RequestHeader(value = "X-Roles") String rolesHeader, @RequestParam("id") UUID id) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin") || roles.contains("product_manager")) {
            return ResponseEntity.ok(orderService.getOrdersByUserId(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@RequestHeader(value = "X-Roles") String rolesHeader, @PathVariable UUID orderId) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin") || roles.contains("product_manager")) {
            return ResponseEntity.ok(orderService.getOrderById(orderId));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}