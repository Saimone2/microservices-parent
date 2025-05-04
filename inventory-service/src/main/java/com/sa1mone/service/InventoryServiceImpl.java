package com.sa1mone.service;

import com.sa1mone.request.StockUpdateMessage;
import com.sa1mone.response.ProductDTO;
import com.sa1mone.entity.Inventory;
import com.sa1mone.entity.Warehouse;
import com.sa1mone.repo.InventoryRepository;
import com.sa1mone.request.InventoryRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final WarehouseService warehouseService;
    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository, WarehouseService warehouseService, RestTemplateBuilder restTemplate, RabbitTemplate rabbitTemplate) {
        this.inventoryRepository = inventoryRepository;
        this.warehouseService = warehouseService;
        this.restTemplate = restTemplate.build();
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Optional<List<Inventory>> getInventoryByProductId(UUID productId) {
        return inventoryRepository.findByProductId(productId);
    }

    @Override
    public boolean isInventoryTableEmpty() {
        return inventoryRepository.count() == 0;
    }

    @Override
    public List<ProductDTO> getTestProducts() {
        ResponseEntity<List<ProductDTO>> response = restTemplate.exchange(
                "http://catalog-service:8082/product/test",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getBody();
    }

    public boolean checkProductExists(UUID productId) {
        try {
            Boolean response = restTemplate.getForObject(
                    "http://catalog-service:8082/management/product/" + productId + "/exists",
                    Boolean.class
            );
            return response != null && response;
        } catch (RestClientException e) {
            return false;
        }
    }

    @Override
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    @Override
    public Inventory addInventory(InventoryRequest request) {
        Warehouse warehouse = warehouseService.findById(request.getWarehouseId())
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        boolean productExists = checkProductExists(request.getProductId());

        if (!productExists) {
            throw new IllegalArgumentException("Product not found");
        }

        Optional<Inventory> existingInventory = inventoryRepository.findByProductIdAndWarehouseId(request.getProductId(), request.getWarehouseId());

        Inventory inventory;
        if (existingInventory.isPresent()) {
            inventory = existingInventory.get();
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() + request.getAvailableQuantity());
        } else {
            inventory = new Inventory();
            inventory.setProductId(request.getProductId());
            inventory.setWarehouse(warehouse);
            inventory.setAvailableQuantity(request.getAvailableQuantity());
            inventory.setReservedQuantity(0);
        }

        Inventory savedInventory = inventoryRepository.save(inventory);
        sendStockUpdate(savedInventory.getProductId(), savedInventory.getAvailableQuantity());

        return savedInventory;
    }

    public void sendStockUpdate(UUID productId, int quantity) {
        StockUpdateMessage message = new StockUpdateMessage(productId.toString(), quantity);
        rabbitTemplate.convertAndSend("inventory.exchange", "inventory.new_stock", message);
    }

    @Override
    public Inventory updateInventory(UUID productId, InventoryRequest request) {
        boolean productExists = checkProductExists(productId);

        if (!productExists) {
            throw new EntityNotFoundException("Product not found");
        }

        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(productId, request.getWarehouseId())
                .orElseThrow(() -> new EntityNotFoundException("No record of the product in stock found"));

        inventory.setAvailableQuantity(request.getAvailableQuantity());
        return inventoryRepository.save(inventory);
    }

    @Override
    public boolean reserveStock(UUID productId, int quantity) {
        List<Inventory> inventories = inventoryRepository.findByProductId(productId).orElseThrow(() ->
                new EntityNotFoundException("Product not found in any warehouse")
        );

        int remainingQuantity = quantity;
        int totalReserved = 0;

        for (Inventory inventory : inventories) {
            if (remainingQuantity <= 0) break;

            int available = inventory.getAvailableQuantity();
            int toReserve = Math.min(available, remainingQuantity);

            inventory.setAvailableQuantity(available - toReserve);
            inventory.setReservedQuantity(inventory.getReservedQuantity() + toReserve);
            inventoryRepository.save(inventory);

            remainingQuantity -= toReserve;
            totalReserved += toReserve;
        }

        if (totalReserved > 0) {
            sendStockReservedMessage(productId, totalReserved);
        }

        return remainingQuantity == 0;
    }

    @Override
    public boolean restoreStock(UUID productId, int quantity) {
        List<Inventory> inventories = inventoryRepository.findByProductId(productId).orElseThrow(() ->
                new EntityNotFoundException("Product not found in any warehouse")
        );

        int remainingQuantity = quantity;
        int totalRestored = 0;

        for (Inventory inventory : inventories) {
            if (remainingQuantity <= 0) break;

            int reserved = inventory.getReservedQuantity();
            int toRestore = Math.min(reserved, remainingQuantity);

            inventory.setReservedQuantity(reserved - toRestore);
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() + toRestore);
            inventoryRepository.save(inventory);

            remainingQuantity -= toRestore;
            totalRestored += toRestore;
        }

        if (totalRestored > 0) {
            sendStockRestoredMessage(productId, totalRestored);
        }

        return remainingQuantity == 0;
    }

    public void sendStockRestoredMessage(UUID productId, int restoredQuantity) {
        rabbitTemplate.convertAndSend("inventory.exchange", "inventory.restored_stock",
                Map.of("productId", productId.toString(), "restoredQuantity", restoredQuantity));
    }

    public void sendStockReservedMessage(UUID productId, int reservedQuantity) {
        rabbitTemplate.convertAndSend("inventory.exchange", "inventory.reserved_stock",
                Map.of("productId", productId.toString(), "reservedQuantity", reservedQuantity));
    }
}