package com.hsp302.shared_english_e_learning_path.config;

import com.hsp302.shared_english_e_learning_path.security.WebSocketAuthInterceptor;
import com.hsp302.shared_english_e_learning_path.security.WebSocketHandshakeInterceptor;

import com.hsp302.shared_english_e_learning_path.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthenticationService authenticationService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable a simple memory-based message broker to carry the greeting messages back to the client
        config.enableSimpleBroker("/topic", "/queue", "/user");
        // Set prefix for messages that are bound for @MessageMapping methods
        config.setApplicationDestinationPrefixes("/app");
        // Enable user-specific destinations
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register STOMP endpoint with handshake interceptor
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Allow all origins for development
                .addInterceptors(new WebSocketHandshakeInterceptor())
                .withSockJS(); // Enable SockJS fallback options
        
        // Add endpoint without SockJS for native WebSocket clients
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .addInterceptors(new WebSocketHandshakeInterceptor());
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Add authentication interceptor
        registration.interceptors(new WebSocketAuthInterceptor(authenticationService));
    }
}
