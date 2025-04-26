package com.sa1mone.controller;

import com.sa1mone.entity.Inventory;
import com.sa1mone.request.InventoryRequest;
import com.sa1mone.service.InventoryService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public List<Inventory> getInventoryByProductId(@PathVariable UUID productId) {
        return inventoryService.getInventoryByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product"));
    }

    @GetMapping("/all")
    public List<Inventory> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    @PostMapping("/add")
    public Inventory addInventory(@RequestBody InventoryRequest request) {
        return inventoryService.addInventory(request);
    }

    @PutMapping
    public Inventory updateInventory(@RequestParam UUID productId, @RequestBody InventoryRequest request) {
        return inventoryService.updateInventory(productId, request);
    }
}