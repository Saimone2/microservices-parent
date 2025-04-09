package com.sa1mone.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserConsumer {

    @RabbitListener(queues = "userQueue")
    public void handleUserUpdate(String message) {
        System.out.println("Received message: " + message);
    }
}