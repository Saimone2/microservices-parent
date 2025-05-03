package com.sa1mone.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StockUpdateMessage {
    private String productId;
    private int quantity;
}
