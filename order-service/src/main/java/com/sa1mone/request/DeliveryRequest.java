package com.sa1mone.request;

import lombok.Data;

import java.util.UUID;

@Data
public class DeliveryRequest {
    private UUID orderId;
    private UUID userId;
    private String deliveryAddress;
}