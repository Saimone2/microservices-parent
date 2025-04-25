package com.sa1mone.service;

import com.sa1mone.dto.ProductDTO;
import com.sa1mone.entity.Inventory;
import com.sa1mone.entity.Warehouse;
import com.sa1mone.repo.InventoryRepository;
import com.sa1mone.request.InventoryRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final WarehouseService warehouseService;
    private final RestTemplate restTemplate;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository, WarehouseService warehouseService, RestTemplateBuilder restTemplate) {
        this.inventoryRepository = inventoryRepository;
        this.warehouseService = warehouseService;
        this.restTemplate = restTemplate.build();
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
    public void createLeftovers(InventoryRequest inventoryRequest) {
        Warehouse warehouse = warehouseService.getWarehouseById(inventoryRequest.getWarehouseId()).orElseThrow(
                () -> new RuntimeException("Warehouse not found"));

        Inventory inventory = new Inventory();
        inventory.setProductId(inventoryRequest.getProductId());
        inventory.setWarehouse(warehouse);
        inventory.setAvailableQuantity(inventoryRequest.getAvailableQuantity());
        inventory.setReservedQuantity(0);

        inventoryRepository.save(inventory);
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
            restTemplate.getForObject(
                    "http://catalog-service:8082/catalog/" + productId + "/exists",
                    Boolean.class
            );
            return true;
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
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));

        boolean productExists = checkProductExists(request.getProductId());
        if (!productExists) {
            throw new IllegalArgumentException("Product not found");
        }

        Optional<Inventory> existingInventory = inventoryRepository.findByProductIdAndWarehouseId(request.getProductId(), request.getWarehouseId());

        if (existingInventory.isPresent()) {
            Inventory inventory = existingInventory.get();
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() + request.getAvailableQuantity());
            return inventoryRepository.save(inventory);
        }

        Inventory newInventory = new Inventory();
        newInventory.setProductId(request.getProductId());
        newInventory.setWarehouse(warehouse);
        newInventory.setAvailableQuantity(request.getAvailableQuantity());
        newInventory.setReservedQuantity(0);

        return inventoryRepository.save(newInventory);
    }

    @Override
    public Inventory updateInventory(UUID productId, InventoryRequest request) {
        boolean productExists = checkProductExists(productId);
        if (!productExists) {
            throw new IllegalArgumentException("Product not found");
        }

        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(productId, request.getWarehouseId())
                .orElseThrow(() -> new EntityNotFoundException("No record of the product in stock found"));

        inventory.setAvailableQuantity(request.getAvailableQuantity());
        return inventoryRepository.save(inventory);
    }
}