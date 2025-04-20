package com.sa1mone.controller;

import com.sa1mone.entity.Order;
import com.sa1mone.request.OrderRequest;
import com.sa1mone.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {
        Order order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

//    @PutMapping("/{orderId}/status")
//    public ResponseEntity<Void> updateOrderStatus(@PathVariable UUID orderId, @RequestBody StatusUpdateRequest request) {
//        orderService.updateOrderStatus(orderId, request.getStatus());
//        return ResponseEntity.ok().build();
//    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable UUID id) {
        return orderService.getOrderById(id);
    }

    @GetMapping("/user/{userId}")
    public List<Order> getUserOrders(@PathVariable UUID userId) {
        return orderService.getOrdersByUserId(userId);
    }
}