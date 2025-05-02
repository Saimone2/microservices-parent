package com.sa1mone.response;

import com.sa1mone.enums.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class OrderResponse {

    private UUID id;
    private String deliveryAddress;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private double totalPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponse> items;

    @Data
    public static class OrderItemResponse {
        private String productName;
        private int quantity;
        private double price;
    }
}
