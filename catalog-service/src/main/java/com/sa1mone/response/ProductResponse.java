package com.sa1mone.response;

import lombok.Data;

@Data
public class ProductResponse {
    private String name;
    private String description;
    private double price;
    private int quantity;
}
