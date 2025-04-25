package com.sa1mone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ProductDTO {
    private UUID id;
    private String name;
    private String description;
    private double price;
    private int stockQuantity;
}