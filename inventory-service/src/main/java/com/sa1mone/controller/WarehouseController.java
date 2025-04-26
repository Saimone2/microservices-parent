package com.sa1mone.controller;

import com.sa1mone.entity.Warehouse;
import com.sa1mone.request.WarehouseRequest;
import com.sa1mone.service.WarehouseService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));
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

    @PostMapping("/{warehouseId}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateWarehouse(@PathVariable UUID warehouseId) {
        warehouseService.deactivateWarehouse(warehouseId);
        return ResponseEntity.ok(Map.of("message", "Warehouse deactivated successfully"));
    }

    @PostMapping("/{warehouseId}/activate")
    public ResponseEntity<Map<String, Object>> activateWarehouse(@PathVariable UUID warehouseId) {
        warehouseService.activateWarehouse(warehouseId);
        return ResponseEntity.ok(Map.of("message", "Warehouse activated successfully"));
    }
}