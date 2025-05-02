package com.sa1mone.service;

import com.sa1mone.entity.Delivery;
import com.sa1mone.enums.DeliveryStatus;
import com.sa1mone.request.DeliveryRequest;
import com.sa1mone.response.DeliveryResponse;

import java.util.List;
import java.util.UUID;

public interface DeliveryService {
    Delivery getDeliveryById(UUID deliveryId);
    List<Delivery> getAllDeliveries();
    DeliveryStatus getDeliveryStatus(UUID orderId);
    UUID createDelivery(DeliveryRequest request);
    List<DeliveryResponse> getUserDeliveriesByEmail(String email);
}