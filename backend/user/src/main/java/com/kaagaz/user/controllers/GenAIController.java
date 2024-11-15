package com.kaagaz.user.controllers;

import com.kaagaz.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/genAI")
@CrossOrigin(origins="http://localhost:5173", allowedHeaders="*")
public class GenAIController {

    @Autowired
    private WebClient.Builder wc;

    @Value("${huggingface.api.key}")
    private String huggingFaceApiKey;

    @Autowired
    private UserService userservice;


    @PostMapping
    public ResponseEntity<?> generateBlogContent(@RequestBody Map<String, String> requestBody) {
        String title = requestBody.get("title");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username= authentication.getName();
        try {
            userservice.isSubscribed(username);
            Mono<String> generatedTextMono = wc.build()
                    .post()
                    .uri("https://api-inference.huggingface.co/models/gpt2")
                    .header("Authorization", "Bearer " + huggingFaceApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue("{\"inputs\": \"" + title + "\"}")
                    .retrieve()
                    .bodyToMono(String.class); // Get the response body as a String

            String generatedText = generatedTextMono.block();

            return ResponseEntity.ok(Map.of("generated_text", generatedText));

        } catch (Exception e) {
            // Log the exception and return a generic error response
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
}
