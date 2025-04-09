package com.sa1mone.messaging;

import com.sa1mone.service.InventoryService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryConsumer {

    public InventoryConsumer(InventoryService inventoryService) {
    }

    @RabbitListener(queues = "orderQueue")
    public void handleOrderEvent(String message) {
        // Приклад обробки повідомлення (JSON можна розпарсити):
        System.out.println("Received message: " + message);
        // Логіка для оновлення запасів
    }
}