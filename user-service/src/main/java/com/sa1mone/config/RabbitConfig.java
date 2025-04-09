package com.sa1mone.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitConfig {
    @Bean
    public Queue userQueue() {
        return new Queue("userQueue");
    }

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange("userExchange");
    }

    @Bean
    public Binding bindingUserQueue(Queue userQueue, TopicExchange userExchange) {
        return BindingBuilder.bind(userQueue).to(userExchange).with("user.updated");
    }
}