package com.kaagaz.subscription.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    public static final String SUBSCRIPTION_QUEUE = "subscriptionQueue";
    public static final String SUBSCRIPTION_EXCHANGE = "subscriptionExchange";
    public static final String ROUTING_KEY = "subscription.key";
    public static final String EXPIRY_REMINDER_QUEUE = "expiryReminderQueue";
    public static final String EXPIRY_REMINDER_ROUTING_KEY = "expiry.reminder.key";

    @Bean
    public Queue queue() {
        return new Queue(SUBSCRIPTION_QUEUE, true);
    }

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(SUBSCRIPTION_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public Queue expiryReminderQueue() {
        return new Queue(EXPIRY_REMINDER_QUEUE, true);
    }

    @Bean
    public Binding expiryReminderBinding(Queue expiryReminderQueue, DirectExchange exchange) {
        return BindingBuilder.bind(expiryReminderQueue).to(exchange).with(EXPIRY_REMINDER_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }



}
