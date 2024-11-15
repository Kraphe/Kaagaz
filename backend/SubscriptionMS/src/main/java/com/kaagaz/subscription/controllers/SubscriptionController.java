package com.kaagaz.subscription.controllers;


import com.kaagaz.subscription.dto.SubscriptionDTO;
import com.kaagaz.subscription.service.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@RestController
@RequestMapping("/subscription")

@CrossOrigin(origins="http://localhost:5173")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    WebClient.Builder wc;

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody SubscriptionDTO subscriptiondto){
        String userServiceUrl = "http://localhost:8080/account/email/exist/"+subscriptiondto.getEmail();
        System.out.println(subscriptiondto);
        System.out.println(userServiceUrl);
        try {
            ResponseEntity<?> response = wc.build()
                    .post()
                    .uri(userServiceUrl)
                    .retrieve()
                    .toEntity(Object.class)
                    .block();

            System.out.println(response);
            subscriptionService.availSubscription(subscriptiondto);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch( Exception e){
            log.error("Some Error occur while saving subscription " + e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("isSubscribed/email/{email}")
    public ResponseEntity<?> subscribe(@PathVariable String email){
        try{
            if(subscriptionService.isSubscribed(email))
                return new ResponseEntity<>("true", HttpStatus.OK);
            return new ResponseEntity<>("false", HttpStatus.NOT_FOUND);
        }
        catch(Exception e){
            log.error("Some error occur  while checking subscription through email"+ e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
