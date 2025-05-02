package com.sa1mone.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WarehouseRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String location;
}