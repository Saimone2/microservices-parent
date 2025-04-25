package com.sa1mone.controller;

import com.sa1mone.entity.Warehouse;
import com.sa1mone.request.WarehouseRequest;
import com.sa1mone.service.WarehouseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/warehouse")
public class WarehouseController {
    private final WarehouseService warehouseService;

    @Autowired
    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping("/{warehouseId}")
    public Warehouse getWarehouseById(@PathVariable UUID warehouseId) {
        return warehouseService.getWarehouseById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
    }

    @GetMapping("/all")
    public List<Warehouse> getAllWarehouses() {
        return warehouseService.getAllWarehouses();
    }

    @PostMapping("/add")
    public Warehouse addWarehouse(@Valid @RequestBody WarehouseRequest warehouseRequest) {
        return warehouseService.addWarehouse(warehouseRequest);
    }

    @PutMapping("/{warehouseId}")
    public ResponseEntity<Warehouse> updateProduct(@PathVariable UUID warehouseId, @Valid @RequestBody WarehouseRequest request) {
        return ResponseEntity.ok(warehouseService.updateWarehouse(warehouseId, request));
    }
}