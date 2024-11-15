package com.kaagaz.subscription.repository;


import com.kaagaz.subscription.entity.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SubscriptionRepository extends MongoRepository<Subscription, String> {
    public Subscription findByEmail(String id);
    public List<Subscription> findByEndDate(LocalDate endDate);
}
