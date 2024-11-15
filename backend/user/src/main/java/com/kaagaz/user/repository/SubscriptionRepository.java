package com.kaagaz.user.repository;

import com.kaagaz.user.entity.Subscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends MongoRepository<Subscription, String> {
    public Subscription findByUserId(String id);
}
