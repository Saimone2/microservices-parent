package com.sa1mone.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class DeliveryRequestDTO {
    @NotNull
    private UUID orderId;

    @NotBlank
    private String address;
}