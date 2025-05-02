package com.sa1mone.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class DeliveryRequest {
    @NotNull
    private UUID orderId;
    @NotNull
    private UUID userId;
    @NotBlank
    private String deliveryAddress;
}