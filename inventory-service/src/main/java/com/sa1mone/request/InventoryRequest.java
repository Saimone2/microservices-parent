package com.sa1mone.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class InventoryRequest {
    @NotNull
    private UUID productId;

    @NotNull
    private UUID warehouseId;

    @Min(0)
    private int availableQuantity;
}