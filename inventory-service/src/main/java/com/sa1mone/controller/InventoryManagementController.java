package com.sa1mone.controller;

import com.sa1mone.request.ReserveStockRequest;
import com.sa1mone.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/management/inventory")
public class InventoryManagementController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryManagementController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/reserve")
    public ResponseEntity<Map<String, Object>> reserveStock(@RequestBody ReserveStockRequest request) {
        boolean reserved = inventoryService.reserveStock(request.getProductId(), request.getQuantity());
        if (reserved) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Stock reserved"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Not enough stock"));
        }
    }

    @PostMapping("/restore")
    public ResponseEntity<Map<String, Object>> restoreStock(@RequestBody ReserveStockRequest request) {
        boolean restored = inventoryService.restoreStock(request.getProductId(), request.getQuantity());
        if (restored) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Stock restored"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "message", "Stock not found or cannot be restored"));
        }
    }
}