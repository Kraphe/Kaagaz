package com.kaagaz.notification.service;


import com.kaagaz.notification.config.RabbitMQConfig;
import com.kaagaz.notification.dto.SubscriptionDTO;
import com.kaagaz.notification.enums.SubscriptionType;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class NotificationService {
    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.SUBSCRIPTION_QUEUE)
    public void receiveMessage(SubscriptionDTO subscriptiondto) {
        System.out.println("aa to gaye RabbitMQ consumer " + subscriptiondto);
        String subject = "Welcome to Kaagaz! Subscription Confirmation";
        String messageBody = String.format(
                "Hello %s,\n\n" +
                        "Thank you for subscribing to Kaagaz! We’re excited to have you on board.\n\n" +
                        "Here are your subscription details:\n" +
                        "- Plan: %s\n" +
                        "- Duration: %s\n" +
                        "- Start Date: %s\n" +
                        "- End Date: %s\n\n" +
                        "You now have access to all the features of your subscription. If you have any questions or need assistance, " +
                        "please don't hesitate to reach out to our support team at \n" +
                        "g70104805@gmail.com.\n\n" +
                        "Thank you for choosing Kaagaz! We look forward to providing you with an excellent experience.\n\n" +
                        "Best regards,\nThe Kaagaz Team\n\n" +
                        "---\nThis is an automated message. Please do not reply to this email.",
                subscriptiondto.getEmail(), subscriptiondto.getSubscriptionType(),
                subscriptiondto.getSubscriptionType()== SubscriptionType.monthly ? "1 Month" : "1 Year",
                subscriptiondto.getStartDate(), subscriptiondto.getEndDate(), LocalTime.MAX
        );
        emailService.sendEmail(subscriptiondto.getEmail(), subject, messageBody);
        System.out.println("Notification sent for subscription: " + subscriptiondto);
    }

    @RabbitListener(queues = RabbitMQConfig.EXPIRY_REMINDER_QUEUE)
    public void receiveMessageForReminder(SubscriptionDTO subscriptiondto) {
        System.out.println(subscriptiondto);
        String subject = "Reminder: Your Subscription Ends Today - Renew Now to Continue Access";
        String formattedEndDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
        String messageBody = String.format(
                "Hello %s,\n\n" +
                        "We wanted to remind you that your subscription with Kaagaz will end today at 11:59 PM. " +
                        "To continue enjoying uninterrupted access to all our features and benefits, we encourage you to renew your subscription.\n\n" +
                        "**Subscription Details:**\n" +
                        "- **Plan**: %s\n" +
                        "- **End Date**: Today, %s\n" +
                        "- **Renewal Options**: Monthly, Yearly\n\n" +
                        "Renewing is easy! Just log in to your account and select your preferred plan.\n\n" +
                        "If you have any questions or need assistance, please reach out to our support team at g70104805@gmail.com.\n\n" +
                        "Thank you for being a valued member of Kaagaz. We look forward to continuing to serve you!\n\n" +
                        "Best regards,\nThe Kaagaz Team\n\n" +
                        "---\nThis is an automated message. Please do not reply directly to this email.",
                subscriptiondto.getEmail(), subscriptiondto.getSubscriptionType(), formattedEndDate);

               emailService.sendEmail(subscriptiondto.getEmail(), subject, messageBody);
        System.out.println("Reminder Notification sent for subscription: " + subscriptiondto);
    }
}
