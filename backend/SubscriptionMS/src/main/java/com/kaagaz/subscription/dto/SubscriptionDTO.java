package com.kaagaz.subscription.dto;


import com.kaagaz.subscription.enums.SubscriptionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {

    private String id;
    private String email;
    private SubscriptionType subscriptionType;
    private Date startDate;
    private Date endDate;
    private boolean isActive=false;
}
