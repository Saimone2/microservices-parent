package com.sa1mone.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class OrderItemDTO {
    @NotNull
    private UUID productId;

    @Min(1)
    private int quantity;
}