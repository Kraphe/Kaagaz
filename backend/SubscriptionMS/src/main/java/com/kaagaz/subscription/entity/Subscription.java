package com.kaagaz.subscription.entity;

import com.kaagaz.subscription.enums.SubscriptionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    @Id
    private String id;

    @NonNull
    private String email;

    @NonNull
    private SubscriptionType subscriptionType;

    @NonNull
    private Date startDate;

    @NonNull
    private Date endDate;

    @NonNull
    private boolean isActive=false;
}
