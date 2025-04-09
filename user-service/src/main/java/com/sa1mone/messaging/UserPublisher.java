package com.sa1mone.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public UserPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserUpdate(String routingKey, String message) {
        rabbitTemplate.convertAndSend("userExchange", routingKey, message);
    }
}