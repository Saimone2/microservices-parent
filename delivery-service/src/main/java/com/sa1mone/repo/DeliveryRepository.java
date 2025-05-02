package com.sa1mone.repo;

import com.sa1mone.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
    Optional<Delivery> findByOrderId(UUID orderId);
    Optional<Delivery> getDeliveryById(UUID id);
    List<Delivery> findByUserId(UUID id);
}