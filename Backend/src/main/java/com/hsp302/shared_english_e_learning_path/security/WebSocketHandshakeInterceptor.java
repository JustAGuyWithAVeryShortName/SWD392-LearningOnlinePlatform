package com.hsp302.shared_english_e_learning_path.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

@Slf4j
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        log.info("WebSocket handshake started for: {}", request.getURI());

        // Extract token from query parameters
        URI uri = request.getURI();
        String query = uri.getQuery();

        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && "token".equals(keyValue[0])) {
                    String token = keyValue[1];
                    log.info("Token found in URL query parameters");
                    attributes.put("token", token);
                    break;
                }

                if (keyValue.length == 2 && "username".equals(keyValue[0])) {
                    String username = keyValue[1];
                    log.info("Username found in URL query parameters: {}", username);
                    attributes.put("username", username);
                }
            }
        }

        // Also check headers for authorization
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("Token found in Authorization header");
            attributes.put("token", token);
        }

        log.info("WebSocket handshake completed successfully");
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
            WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("WebSocket handshake failed", exception);
        } else {
            log.info("WebSocket handshake successful for: {}", request.getURI());
        }
    }
}
