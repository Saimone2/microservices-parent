package com.sa1mone.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WarehouseRequest {
    @NotNull
    private String name;

    @NotNull
    private String location;
}