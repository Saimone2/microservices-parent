package com.sa1mone.service;

import com.sa1mone.entity.Warehouse;
import com.sa1mone.repo.WarehouseRepository;
import com.sa1mone.request.WarehouseRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Autowired
    public WarehouseServiceImpl(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Override
    public Optional<Warehouse> getWarehouseById(UUID warehouseId) {
        return warehouseRepository.findById(warehouseId);
    }

    @Override
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    @Override
    public Warehouse addWarehouse(WarehouseRequest warehouseRequest) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(warehouseRequest.getName());
        warehouse.setLocation(warehouseRequest.getLocation());

        return warehouseRepository.save(warehouse);
    }

    @Override
    public boolean isWarehouseTableEmpty() {
        return warehouseRepository.count() == 0;
    }

    @Override
    public Warehouse updateWarehouse(UUID warehouseId, WarehouseRequest request) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found"));

        if (warehouseRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Warehouse from request already exists");
        }

        warehouse.setName(request.getName());
        warehouse.setLocation(request.getLocation());
        return warehouseRepository.save(warehouse);
    }

    @Override
    public Optional<Warehouse> findById(UUID warehouseId) {
        return warehouseRepository.findById(warehouseId);
    }
}