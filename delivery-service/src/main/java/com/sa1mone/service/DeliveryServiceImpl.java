package com.sa1mone.service;

import com.sa1mone.entity.Delivery;
import com.sa1mone.repo.DeliveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;

    @Autowired
    public DeliveryServiceImpl(DeliveryRepository deliveryRepository) {
        this.deliveryRepository = deliveryRepository;
    }

    @Override
    public Delivery createDelivery(Long orderId, String address) {
        Delivery delivery = new Delivery();
        delivery.setOrderId(orderId);
        delivery.setAddress(address);
        delivery.setStatus("Pending");
        return deliveryRepository.save(delivery);
    }

    @Override
    public Delivery updateDeliveryStatus(Long deliveryId, String status) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));
        delivery.setStatus(status);
        return deliveryRepository.save(delivery);
    }

    @Override
    public Optional<Delivery> getDeliveryByOrderId(Long orderId) {
        return deliveryRepository.findByOrderId(orderId);
    }
}
