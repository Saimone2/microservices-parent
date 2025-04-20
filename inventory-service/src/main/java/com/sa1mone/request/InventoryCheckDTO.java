package com.sa1mone.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class InventoryCheckDTO {
    @NotNull
    private UUID productId;
}
