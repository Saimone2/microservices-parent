package com.sa1mone.service;

import com.sa1mone.entity.Inventory;

import java.util.Optional;
import java.util.UUID;

public interface InventoryService {
    Optional<Inventory> getInventoryByProductId(UUID productId);
    Inventory updateInventory(UUID productId, Integer quantityChange);
    Inventory addInventory(UUID productId, Integer quantity);
}