package com.sa1mone.service;

import com.sa1mone.entity.Delivery;
import com.sa1mone.enums.DeliveryStatus;
import com.sa1mone.repo.DeliveryRepository;
import com.sa1mone.request.DeliveryRequest;
import com.sa1mone.response.DeliveryResponse;
import com.sa1mone.response.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public DeliveryServiceImpl(DeliveryRepository deliveryRepository, RestTemplateBuilder restTemplateBuilder) {
        this.deliveryRepository = deliveryRepository;
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Delivery getDeliveryById(UUID deliveryId) {
        return deliveryRepository.getDeliveryById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found"));
    }

    @Override
    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    @Override
    public DeliveryStatus getDeliveryStatus(UUID orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found for this order"));
        return delivery.getStatus();
    }

    @Override
    public UUID createDelivery(DeliveryRequest request) {
        Delivery delivery = new Delivery();
        LocalDateTime dateTime = LocalDateTime.now();

        delivery.setOrderId(request.getOrderId());
        delivery.setUserId(request.getUserId());
        delivery.setAddress(request.getDeliveryAddress());
        delivery.setStatus(DeliveryStatus.PENDING);
        delivery.setCreatedAt(dateTime);
        delivery.setUpdatedAt(dateTime);

        Delivery savedDelivery = deliveryRepository.save(delivery);
        return savedDelivery.getId();
    }

    @Override
    public List<DeliveryResponse> getUserDeliveriesByEmail(String email) {
        UserResponse userResponse = fetchUserInfoByEmail(email);

        List<Delivery> deliveries = deliveryRepository.findByUserId(userResponse.getId());
        return deliveries.stream()
                .map(this::mapDeliveryToResponse)
                .collect(Collectors.toList());

    }

    private DeliveryResponse mapDeliveryToResponse(Delivery delivery) {
        DeliveryResponse response = new DeliveryResponse();
        response.setId(delivery.getId());
        response.setOrderId(delivery.getOrderId());
        response.setDeliveryAddress(delivery.getAddress());
        response.setStatus(delivery.getStatus());
        return response;
    }

    private UserResponse fetchUserInfoByEmail(String email) {
        String userServiceUrl = "http://user-service:8081/management/user/find-by-email?email=" + email;

        ResponseEntity<UserResponse> response = restTemplate.getForEntity(userServiceUrl, UserResponse.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new EntityNotFoundException("User not found");
        }
    }
}