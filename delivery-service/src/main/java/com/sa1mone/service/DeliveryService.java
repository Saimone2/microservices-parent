package com.sa1mone.service;

import com.sa1mone.entity.Delivery;
import com.sa1mone.enums.DeliveryStatus;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryService {
    Delivery createDelivery(UUID orderId, String address);
    Delivery updateDeliveryStatus(UUID deliveryId, DeliveryStatus status);
    Optional<Delivery> getDeliveryByOrderId(UUID orderId);
}
