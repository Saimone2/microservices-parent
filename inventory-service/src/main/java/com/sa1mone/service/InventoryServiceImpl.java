package com.sa1mone.service;

import com.sa1mone.entity.Inventory;
import com.sa1mone.repo.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public Optional<Inventory> getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId);
    }

    @Override
    public Inventory updateInventory(Long productId, Integer quantityChange) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Product not found in inventory"));
        inventory.setQuantity(inventory.getQuantity() + quantityChange);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory addInventory(Long productId, Integer quantity) {
        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setQuantity(quantity);
        return inventoryRepository.save(inventory);
    }
}