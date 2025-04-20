package com.sa1mone.service;

import com.sa1mone.entity.Order;
import com.sa1mone.request.OrderRequest;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order createOrder(OrderRequest order);
    Order getOrderById(UUID id);
    List<Order> getOrdersByUserId(UUID userId);
}