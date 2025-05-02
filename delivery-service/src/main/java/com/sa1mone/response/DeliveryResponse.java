package com.sa1mone.response;

import com.sa1mone.enums.DeliveryStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class DeliveryResponse {
    private UUID id;
    private UUID orderId;
    private String deliveryAddress;
    private DeliveryStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}