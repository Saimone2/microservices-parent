package com.sa1mone.service;

import com.sa1mone.entity.Payment;

import java.util.List;

public interface PaymentService {
    Payment processPayment(Payment payment);
    Payment getPaymentById(Long id);
    List<Payment> getPaymentsByOrderId(Long orderId);
}