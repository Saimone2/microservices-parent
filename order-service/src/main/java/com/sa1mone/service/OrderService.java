package com.sa1mone.service;

import com.sa1mone.entity.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(Order order);
    Order getOrderById(Long id);
    List<Order> getOrdersByUserId(Long userId);
}