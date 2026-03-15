package com.hsp302.shared_english_e_learning_path.security;

import com.hsp302.shared_english_e_learning_path.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final AuthenticationService authenticationService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("WebSocket CONNECT attempt from session: {}", accessor.getSessionId());

            Principal existingPrincipal = accessor.getUser();
            if (existingPrincipal != null && StringUtils.hasText(existingPrincipal.getName())
                    && !"anonymous".equalsIgnoreCase(existingPrincipal.getName())) {
                log.info("Keeping existing WebSocket principal from handshake: {}", existingPrincipal.getName());
                return message;
            }

            String token = null;

            // Method 1: Try to get token from Authorization header
            List<String> authHeaders = accessor.getNativeHeader("Authorization");
            if (authHeaders != null && !authHeaders.isEmpty()) {
                String authHeader = authHeaders.get(0);
                if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                    log.info("Token found in Authorization header");
                }
            }

            // Method 2: Try to get token from custom header
            if (token == null) {
                List<String> tokenParams = accessor.getNativeHeader("token");
                if (tokenParams != null && !tokenParams.isEmpty()) {
                    token = tokenParams.get(0);
                    log.info("Token found in token header");
                }
            }

            // Method 3: Try to get from URL query parameters (from handshake)
            if (token == null) {
                Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                if (sessionAttributes != null) {
                    token = (String) sessionAttributes.get("token");
                    if (token != null) {
                        log.info("Token found in session attributes from handshake");
                    }
                }
            }

            String fallbackUsername = null;
            List<String> usernameHeaders = accessor.getNativeHeader("username");
            if (usernameHeaders != null && !usernameHeaders.isEmpty() && StringUtils.hasText(usernameHeaders.get(0))) {
                fallbackUsername = usernameHeaders.get(0);
                log.info("Username fallback found in CONNECT header: {}", fallbackUsername);
            }
            if (!StringUtils.hasText(fallbackUsername)) {
                Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                if (sessionAttributes != null) {
                    Object usernameAttr = sessionAttributes.get("username");
                    if (usernameAttr instanceof String username && StringUtils.hasText(username)) {
                        fallbackUsername = username;
                        log.info("Username fallback found in handshake attributes: {}", fallbackUsername);
                    }
                }
            }

            // TEMPORARY: Allow connections without token for testing
            if (!StringUtils.hasText(token)) {
                log.warn("No token provided for WebSocket connection - allowing for testing");
                String principalName = StringUtils.hasText(fallbackUsername) ? fallbackUsername : null;
                if (!StringUtils.hasText(principalName) && existingPrincipal != null
                        && StringUtils.hasText(existingPrincipal.getName())) {
                    principalName = existingPrincipal.getName();
                }
                if (!StringUtils.hasText(principalName)) {
                    principalName = "anonymous";
                }
                UsernamePasswordAuthenticationToken testAuth = new UsernamePasswordAuthenticationToken(
                        principalName,
                        null,
                        java.util.Collections.emptyList());
                accessor.setUser(testAuth);
                return message;
            }

            try {
                log.info("Attempting to validate WebSocket token: {}",
                        token.substring(0, Math.min(token.length(), 10)) + "...");
                UserDetails userDetails = authenticationService.validateToken(token);

                if (userDetails != null) {
                    log.info("WebSocket authentication successful for user: {}", userDetails.getUsername());

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    accessor.setUser(authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.warn("WebSocket authentication failed: Invalid token");
                    // TEMPORARY: Allow even with invalid token for testing
                    String principalName = StringUtils.hasText(fallbackUsername) ? fallbackUsername : "anonymous";
                    UsernamePasswordAuthenticationToken testAuth = new UsernamePasswordAuthenticationToken(
                            principalName,
                            null,
                            java.util.Collections.emptyList());
                    accessor.setUser(testAuth);
                }
            } catch (Exception e) {
                log.error("WebSocket authentication error: {}", e.getMessage());
                // TEMPORARY: Allow even with auth errors for testing
                String principalName = StringUtils.hasText(fallbackUsername) ? fallbackUsername : "anonymous";
                UsernamePasswordAuthenticationToken testAuth = new UsernamePasswordAuthenticationToken(
                        principalName,
                        null,
                        java.util.Collections.emptyList());
                accessor.setUser(testAuth);
            }
        }

        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            Principal user = accessor.getUser();
            if (user != null) {
                log.info("WebSocket connection established for user: {}", user.getName());
            }
        }
    }
}
