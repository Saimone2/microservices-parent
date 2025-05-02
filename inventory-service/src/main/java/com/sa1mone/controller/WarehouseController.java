package com.sa1mone.controller;

import com.sa1mone.entity.Warehouse;
import com.sa1mone.request.WarehouseRequest;
import com.sa1mone.service.WarehouseService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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
    public ResponseEntity<Warehouse> getWarehouseById(@RequestHeader(value = "X-Roles") String rolesHeader, @PathVariable UUID warehouseId) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin") || roles.contains("product_manager")) {
            return ResponseEntity.ok(warehouseService.getWarehouseById(warehouseId)
                    .orElseThrow(() -> new EntityNotFoundException("Warehouse not found")));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Warehouse>> getAllWarehouses(@RequestHeader(value = "X-Roles") String rolesHeader) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin") || roles.contains("product_manager")) {
            return ResponseEntity.ok(warehouseService.getAllWarehouses());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Warehouse> addWarehouse(@RequestHeader(value = "X-Roles") String rolesHeader, @Valid @RequestBody WarehouseRequest warehouseRequest) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin") || roles.contains("product_manager")) {
            return ResponseEntity.ok(warehouseService.addWarehouse(warehouseRequest));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("/{warehouseId}")
    public ResponseEntity<Warehouse> updateProduct(@RequestHeader(value = "X-Roles") String rolesHeader, @PathVariable UUID warehouseId, @Valid @RequestBody WarehouseRequest request) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin") || roles.contains("product_manager")) {
            return ResponseEntity.ok(warehouseService.updateWarehouse(warehouseId, request));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/{warehouseId}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateWarehouse(@RequestHeader(value = "X-Roles") String rolesHeader, @PathVariable UUID warehouseId) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin") || roles.contains("product_manager")) {
            warehouseService.deactivateWarehouse(warehouseId);
            return ResponseEntity.ok(Map.of("message", "Warehouse deactivated successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/{warehouseId}/activate")
    public ResponseEntity<Map<String, Object>> activateWarehouse(@RequestHeader(value = "X-Roles") String rolesHeader, @PathVariable UUID warehouseId) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin") || roles.contains("product_manager")) {
            warehouseService.activateWarehouse(warehouseId);
            return ResponseEntity.ok(Map.of("message", "Warehouse activated successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}