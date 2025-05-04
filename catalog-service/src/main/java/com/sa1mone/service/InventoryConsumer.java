package com.sa1mone.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class InventoryConsumer {
    private final ProductService productService;

    public InventoryConsumer(ProductService productService) {
        this.productService = productService;
    }

    @RabbitListener(queues = "inventory.queue.new_stock")
    public void addNewStock(Map<String, Object> message) {
        UUID productId = UUID.fromString((String) message.get("productId"));
        int quantity = (int) message.get("quantity");
        productService.updateProductStock(productId, quantity);
    }

    @RabbitListener(queues = "inventory.queue.reserved_stock")
    public void reserveStock(Map<String, Object> message) {
        UUID productId = UUID.fromString((String) message.get("productId"));
        int reservedQuantity = (int) message.get("reservedQuantity");
        productService.reserveProductStock(productId, reservedQuantity);
    }

    @RabbitListener(queues = "inventory.queue.restored_stock")
    public void handleRestoredStockMessage(Map<String, Object> message) {
        UUID productId = UUID.fromString((String) message.get("productId"));
        int restoredQuantity = (int) message.get("restoredQuantity");
        productService.restoreProductStock(productId, restoredQuantity);
    }
}