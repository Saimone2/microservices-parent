package com.sa1mone.service;

import com.sa1mone.entity.Warehouse;
import com.sa1mone.request.WarehouseRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WarehouseService {
    Optional<Warehouse> getWarehouseById(UUID warehouseId);
    List<Warehouse> getAllWarehouses();
    Warehouse addWarehouse(WarehouseRequest warehouseRequest);
    boolean isWarehouseTableEmpty();
    Warehouse updateWarehouse(UUID warehouseId, WarehouseRequest request);
    Optional<Warehouse> findById(UUID warehouseId);
    void deactivateWarehouse(UUID warehouseId);
    void activateWarehouse(UUID warehouseId);
}