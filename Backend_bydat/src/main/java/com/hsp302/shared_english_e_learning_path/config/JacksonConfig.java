package com.hsp302.shared_english_e_learning_path.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    // Temporarily disable custom ObjectMapper to fix login issue
    // @Bean
    // @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register Java 8 time module for LocalDate, LocalDateTime, etc.
        mapper.registerModule(new JavaTimeModule());
        
        // Configure to handle lazy loading issues
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
        // Disable writing dates as timestamps (use ISO format instead)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return mapper;
    }
}
