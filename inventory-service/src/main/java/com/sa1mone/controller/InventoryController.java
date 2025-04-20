package com.sa1mone.controller;

import com.sa1mone.entity.Inventory;
import com.sa1mone.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{productId}")
    public Inventory getInventory(@PathVariable UUID productId) {
        return inventoryService.getInventoryByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product"));
    }

    @PostMapping
    public Inventory addInventory(@RequestParam UUID productId, @RequestParam Integer quantity) {
        return inventoryService.addInventory(productId, quantity);
    }

    @PutMapping
    public Inventory updateInventory(@RequestParam UUID productId, @RequestParam Integer quantityChange) {
        return inventoryService.updateInventory(productId, quantityChange);
    }
}