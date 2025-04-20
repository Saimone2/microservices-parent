package com.sa1mone.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public class OrderRequest {
    @NotNull
    private UUID userId;

    @NotEmpty
    private List<OrderItemDTO> items;

    @NotBlank
    private String deliveryAddress;
}