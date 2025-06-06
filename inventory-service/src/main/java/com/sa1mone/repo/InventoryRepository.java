package com.sa1mone.repo;

import com.sa1mone.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    Optional<List<Inventory>> findByProductId(UUID productId);
    Optional<Inventory> findByProductIdAndWarehouseId(UUID productId, UUID warehouseId);
    List<Inventory> findByWarehouseId(UUID warehouseId);
}