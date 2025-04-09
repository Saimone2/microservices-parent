package com.sa1mone.service;

import com.sa1mone.entity.Inventory;

import java.util.Optional;

public interface InventoryService {
    Optional<Inventory> getInventoryByProductId(Long productId);
    Inventory updateInventory(Long productId, Integer quantityChange);
    Inventory addInventory(Long productId, Integer quantity);
}