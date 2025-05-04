package com.sa1mone.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public DirectExchange inventoryExchange() {
        return new DirectExchange("inventory.exchange");
    }

    @Bean
    public Queue newStockQueue() {
        return new Queue("inventory.queue.new_stock", true);
    }

    @Bean
    public Queue reservedStockQueue() {
        return new Queue("inventory.queue.reserved_stock", true);
    }

    @Bean
    public Queue restoredStockQueue() {
        return new Queue("inventory.queue.restored_stock", true);
    }

    @Bean
    public Binding newStockBinding(Queue newStockQueue, DirectExchange inventoryExchange) {
        return BindingBuilder.bind(newStockQueue).to(inventoryExchange).with("inventory.new_stock");
    }

    @Bean
    public Binding reservedStockBinding(Queue reservedStockQueue, DirectExchange inventoryExchange) {
        return BindingBuilder.bind(reservedStockQueue).to(inventoryExchange).with("inventory.reserved_stock");
    }

    @Bean
    public Binding restoredStockBinding(Queue restoredStockQueue, DirectExchange inventoryExchange) {
        return BindingBuilder.bind(restoredStockQueue).to(inventoryExchange).with("inventory.restored_stock");
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}