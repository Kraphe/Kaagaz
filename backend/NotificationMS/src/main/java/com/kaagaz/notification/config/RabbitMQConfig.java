package com.kaagaz.notification.config;

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
    public static final String EXPIRY_REMINDER_QUEUE = "expiryReminderQueue";

    @Bean
    public Queue queue() {
        return new Queue(SUBSCRIPTION_QUEUE, true);  // Ensure queue durability
    }

    @Bean
    public Queue expiryReminderQueue() {
        return new Queue(EXPIRY_REMINDER_QUEUE, true);
    }


    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();  // Converts messages to/from JSON
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
