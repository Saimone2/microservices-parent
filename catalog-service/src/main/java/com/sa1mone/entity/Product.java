package com.sa1mone.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NotBlank
    private String name;

    @Column(length = 1000)
    private String description;

    @Min(0)
    private double price;

    @Min(0)
    private int stockQuantity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}