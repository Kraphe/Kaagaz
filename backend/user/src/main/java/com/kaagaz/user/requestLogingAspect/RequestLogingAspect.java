package com.kaagaz.user.requestLogingAspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestBody;

@Aspect
@ControllerAdvice
public class RequestLogingAspect {
    private static final Logger logger = LoggerFactory.getLogger(RequestLogingAspect.class);

    @Before("execution(* com.yourpackage.controller..*(.., ..))") // Match all methods in controllers
    public void logRequestBody(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs(); // Get the method arguments
        for (Object arg : args) {
            if (arg != null) {
                logger.info("Request Body: {}", arg); // Log the argument, which can include the request body
            }
        }
    }
}
