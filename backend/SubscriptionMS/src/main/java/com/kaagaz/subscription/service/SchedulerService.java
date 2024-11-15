package com.kaagaz.subscription.service;

import com.kaagaz.subscription.config.RabbitMQConfig;
import com.kaagaz.subscription.dto.SubscriptionDTO;
import com.kaagaz.subscription.entity.Subscription;
import com.kaagaz.subscription.repository.SubscriptionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class SchedulerService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final ModelMapper modelMapper = new ModelMapper();

    @Scheduled(cron = "0 0 6 * * ?")
    public void sendSubscriptionExpiryEmails() {
            System.out.println("Sheduler start");
        LocalDate today = LocalDate.now();


        List<Subscription> usersWithExpiringSubscriptions = subscriptionRepository.findAll();
        for (Subscription subscription : usersWithExpiringSubscriptions) {
            SubscriptionDTO subscriptiondto= modelMapper.map(subscription, SubscriptionDTO.class);
            rabbitTemplate.convertAndSend(RabbitMQConfig.SUBSCRIPTION_EXCHANGE,
                    RabbitMQConfig.EXPIRY_REMINDER_ROUTING_KEY,
                    subscriptiondto);
        }
    }
}
