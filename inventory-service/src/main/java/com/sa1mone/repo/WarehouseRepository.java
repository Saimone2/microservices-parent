package com.sa1mone.repo;

import com.sa1mone.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
    boolean existsByName(String name);
}