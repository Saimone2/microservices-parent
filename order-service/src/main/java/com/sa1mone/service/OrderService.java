package com.sa1mone.service;

import com.sa1mone.entity.Order;
import com.sa1mone.enums.OrderStatus;
import com.sa1mone.request.OrderRequest;
import com.sa1mone.response.OrderResponse;

import java.util.List;
import java.util.UUID;


public interface OrderService {
    Order getOrderById(UUID id);
    List<Order> getOrdersByUserId(UUID userId);
    Order createOrder(String email, OrderRequest request);
    List<OrderResponse> getAuthenticatedUserOrders(String email);
    OrderStatus checkOrderStatus(String email, UUID orderId);
    OrderStatus cancelOrder(String email, UUID orderId);
}