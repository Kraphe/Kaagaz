package com.kaagaz.subscription.service;


import com.kaagaz.subscription.config.RabbitMQConfig;
import com.kaagaz.subscription.dto.SubscriptionDTO;
import com.kaagaz.subscription.entity.Subscription;
import com.kaagaz.subscription.enums.SubscriptionType;
import com.kaagaz.subscription.repository.SubscriptionRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static java.util.Collections.max;

@Slf4j
@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final ModelMapper modelMapper = new ModelMapper();

    public boolean isSubscribed(String email){
        Subscription subscription = subscriptionRepository.findByEmail(email);
        if (subscription != null && subscription.isActive()) {
            return subscription.getEndDate().after(new Date());
        }
        return false;
    }

    public void availSubscription(SubscriptionDTO subscriptiondto) {
        LocalDate currentDate = LocalDate.now();
        System.out.println(subscriptiondto);
        long monthsToAdd = (subscriptiondto.getSubscriptionType() == SubscriptionType.monthly) ? 1 : 12;
        Optional<Subscription> subs = Optional.ofNullable(subscriptionRepository.findByEmail(subscriptiondto.getEmail()));

        if (subs.isPresent()) {
            Subscription subscription = subs.get();
            subscription.setEmail(subscriptiondto.getEmail());
            subscription.setSubscriptionType((subscriptiondto.getSubscriptionType()));
            LocalDate endDate = subscription.getEndDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate()
                    .isAfter(currentDate) ?
                    subscription.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : currentDate;

            subscription.setEndDate(Date.from(LocalDateTime.of(endDate.plusMonths(monthsToAdd), LocalTime.MAX)
                    .atZone(ZoneId.systemDefault()).toInstant()));
            subscription.setStartDate(Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            subscription.setActive(true);
            subscriptionRepository.save(subscription);
            subscriptiondto= modelMapper.map(subscription,SubscriptionDTO.class);
        } else {

            Subscription subscription = new Subscription();
            subscription.setEmail(subscriptiondto.getEmail());
            subscription.setSubscriptionType((subscriptiondto.getSubscriptionType()));
            subscription.setStartDate(Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            subscription.setEndDate(Date.from(LocalDateTime.of(currentDate.plusMonths(monthsToAdd), LocalTime.MAX)
                    .atZone(ZoneId.systemDefault()).toInstant()));
            subscription.setActive(true);
            subscriptionRepository.save(subscription);
            subscriptiondto= modelMapper.map(subscription,SubscriptionDTO.class);
        }
        System.out.println(subscriptiondto);
         rabbitTemplate.convertAndSend(RabbitMQConfig.SUBSCRIPTION_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                subscriptiondto);
    }
}
