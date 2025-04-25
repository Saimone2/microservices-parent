package com.sa1mone.service;

import com.sa1mone.dto.ProductDTO;
import com.sa1mone.entity.Inventory;
import com.sa1mone.request.InventoryRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryService {
    Optional<List<Inventory>> getInventoryByProductId(UUID productId);
    boolean isInventoryTableEmpty();
    void createLeftovers(InventoryRequest inventoryRequest);
    List<ProductDTO> getTestProducts();
    List<Inventory> getAllInventory();
    Inventory addInventory(InventoryRequest request);
    Inventory updateInventory(UUID productId, InventoryRequest request);
}