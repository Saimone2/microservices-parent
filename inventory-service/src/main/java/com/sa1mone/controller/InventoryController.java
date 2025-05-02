package com.sa1mone.controller;

import com.sa1mone.entity.Inventory;
import com.sa1mone.request.InventoryRequest;
import com.sa1mone.service.InventoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<Inventory>> getInventoryByProductId(@RequestHeader(value = "X-Roles") String rolesHeader, @PathVariable UUID productId) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin") || roles.contains("product_manager")) {
            return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product")));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Inventory>> getAllInventory(@RequestHeader(value = "X-Roles") String rolesHeader) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin") || roles.contains("product_manager")) {
            return ResponseEntity.ok(inventoryService.getAllInventory());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Inventory> addInventory(@RequestHeader(value = "X-Roles") String rolesHeader, @RequestBody InventoryRequest request) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin") || roles.contains("product_manager")) {
            return ResponseEntity.ok(inventoryService.addInventory(request));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping
    public ResponseEntity<Inventory> updateInventory(@RequestHeader(value = "X-Roles") String rolesHeader, @RequestParam UUID productId, @RequestBody InventoryRequest request) {
        List<String> roles = Arrays.asList(rolesHeader.split(","));
        if (roles.contains("admin") || roles.contains("product_manager")) {
            return ResponseEntity.ok(inventoryService.updateInventory(productId, request));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}