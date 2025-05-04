package com.sa1mone.controller;

import com.sa1mone.entity.Order;
import com.sa1mone.enums.OrderStatus;
import com.sa1mone.request.OrderRequest;
import com.sa1mone.response.OrderResponse;
import com.sa1mone.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestHeader("X-User-Email") String email, @Valid @RequestBody OrderRequest request) {
        Order order = orderService.createOrder(email, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/me")
    public ResponseEntity<List<OrderResponse>> getAuthenticatedUserOrders(@RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(orderService.getAuthenticatedUserOrders(email));
    }

    @GetMapping("/{orderId}/status")
    public ResponseEntity<Map<String, Object>> checkOrderStatus(@RequestHeader("X-User-Email") String email, @PathVariable("orderId") UUID orderId) {
        OrderStatus status = orderService.checkOrderStatus(email, orderId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "orderId", orderId,
                "status", status.toString()
        ));
    }

    @DeleteMapping("/{orderId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelOrder(@RequestHeader("X-User-Email") String email, @PathVariable("orderId") UUID orderId) {
        OrderStatus status = orderService.cancelOrder(email, orderId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "orderId", orderId,
                "status", status.toString()
        ));
    }
}