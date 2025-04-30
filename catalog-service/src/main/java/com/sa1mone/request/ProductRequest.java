package com.sa1mone.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductRequest {
    @NotBlank
    private String name;

    @Column(length = 1000)
    private String description;

    @Min(0)
    private double price;
}