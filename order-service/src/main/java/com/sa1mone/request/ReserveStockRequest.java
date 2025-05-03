package com.sa1mone.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReserveStockRequest {
    private UUID productId;
    private int quantity;
}