package com.sa1mone.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class OrderRequest {
    @NotBlank(message = "Delivery address cannot be blank")
    private String deliveryAddress;

    @NotNull(message = "Order items cannot be null")
    @Size(min = 1, message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        @NotNull(message = "Product ID cannot be null")
        private UUID productId;

        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;
    }
}