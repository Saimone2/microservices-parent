package com.sa1mone.service;

import com.sa1mone.entity.Delivery;

import java.util.Optional;

public interface DeliveryService {
    Delivery createDelivery(Long orderId, String address);
    Delivery updateDeliveryStatus(Long deliveryId, String status);
    Optional<Delivery> getDeliveryByOrderId(Long orderId);
}
