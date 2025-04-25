package com.sa1mone.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "warehouse")
@Data
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    private String location;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Inventory> inventoryList;
}