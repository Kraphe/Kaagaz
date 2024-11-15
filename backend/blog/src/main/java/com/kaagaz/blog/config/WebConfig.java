package com.kaagaz.blog.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Allow all paths
                .allowedOrigins("*") // Allow all origins
                .allowedMethods("*") // Allow all HTTP methods (GET, POST, etc.)
                .allowedOriginPatterns("http://localhost:5173") // Specify trusted origins
                .allowedHeaders("*") // Allow all headers
                .maxAge(3600); // Set cache duration for preflight requests
    }
}
