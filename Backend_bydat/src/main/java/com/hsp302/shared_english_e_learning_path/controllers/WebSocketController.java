package com.hsp302.shared_english_e_learning_path.controllers;

import com.hsp302.shared_english_e_learning_path.services.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    /**
     * REST endpoint for WebSocket info/status - useful for debugging
     */
    @GetMapping("/ws/info")
    @ResponseBody
    public Map<String, Object> getWebSocketInfo(@RequestParam(required = false) String token) {
        log.info("WebSocket info endpoint accessed with token present: {}", token != null);
        return Map.of(
            "status", "WebSocket server is running",
            "timestamp", LocalDateTime.now(),
            "endpoints", Map.of(
                "connect", "/ws",
                "topics", Map.of(
                    "private", "/user/queue/private"
                )
            ),
            "tokenReceived", token != null,
            "authRequired", false, // Temporary for testing
            "testMode", true
        );
    }
    
    /**
     * Simple ping endpoint for connection testing
     */
    @GetMapping("/ws/ping")
    @ResponseBody
    public Map<String, Object> ping() {
        return Map.of(
            "status", "pong",
            "timestamp", LocalDateTime.now()
        );
    }

    /**
     * Handle private messages sent to /app/private - now with database persistence
     */
    @MessageMapping("/private")
    public void sendPrivateMessage(@Payload PrivateMessage message, Principal principal) {
        log.info("=== PRIVATE MESSAGE RECEIVED ===");
        log.info("From: {}", principal != null ? principal.getName() : "NULL_PRINCIPAL");
        log.info("To: {}", message.getRecipient());
        log.info("Room ID: {}", message.getRoomId());
        log.info("Content: {}", message.getContent());
        
        try {
            String senderUsername = principal != null ? principal.getName() : "anonymous";
            
            // Save message to database and send via WebSocket
            com.hsp302.shared_english_e_learning_path.domain.entities.ChatMessage savedMessage = chatService.sendMessage(
                message.getRoomId(), 
                senderUsername,
                message.getRecipient(), 
                message.getContent(), 
                com.hsp302.shared_english_e_learning_path.domain.entities.ChatMessage.MessageType.TEXT
            );
            
            log.info("Message saved successfully with ID: {}", savedMessage.getMessageId());
            
        } catch (Exception e) {
            log.error("Error processing private message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send message: " + e.getMessage());
        }
    }

    /**
     * Test endpoint for authentication
     */
    @MessageMapping("/test")
    public void testAuth(Principal principal) {
        log.info("WebSocket authentication test successful for user: {}", principal.getName());
        
        ChatMessage response = ChatMessage.builder()
            .content("Authentication test successful!")
            .sender("System")
            .timestamp(LocalDateTime.now())
            .type("AUTH_TEST")
            .build();
            
        messagingTemplate.convertAndSendToUser(
            principal.getName(), 
            "/queue/private", 
            response
        );
    }
    
    /**
     * Handle typing indicators
     */
    @MessageMapping("/typing")
    public void handleTyping(@Payload TypingMessage message, Principal principal) {
        log.info("User {} typing in room {} to {}: {}", 
                principal.getName(), message.getRoomId(), message.getRecipient(), message.isTyping());
        
        chatService.sendTypingIndicator(
            message.getRoomId(), 
            principal.getName(), 
            message.getRecipient(), 
            message.isTyping()
        );
    }
    
    /**
     * Handle read receipts
     */
    @MessageMapping("/read")
    public void handleReadReceipt(@Payload ReadReceiptMessage message, Principal principal) {
        log.info("User {} marking messages as read in room {}", principal.getName(), message.getRoomId());
        
        chatService.markMessagesAsRead(message.getRoomId(), principal.getName());
    }

    // Inner classes for message types
    public static class ChatMessage {
        private String content;
        private String sender;
        private LocalDateTime timestamp;
        private String type;

        // Constructors
        public ChatMessage() {}
        
        public ChatMessage(String content, String sender, LocalDateTime timestamp, String type) {
            this.content = content;
            this.sender = sender;
            this.timestamp = timestamp;
            this.type = type;
        }

        // Builder pattern
        public static ChatMessageBuilder builder() {
            return new ChatMessageBuilder();
        }

        // Getters and setters
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getSender() { return sender; }
        public void setSender(String sender) { this.sender = sender; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public static class ChatMessageBuilder {
            private String content;
            private String sender;
            private LocalDateTime timestamp;
            private String type;

            public ChatMessageBuilder content(String content) {
                this.content = content;
                return this;
            }

            public ChatMessageBuilder sender(String sender) {
                this.sender = sender;
                return this;
            }

            public ChatMessageBuilder timestamp(LocalDateTime timestamp) {
                this.timestamp = timestamp;
                return this;
            }

            public ChatMessageBuilder type(String type) {
                this.type = type;
                return this;
            }

            public ChatMessage build() {
                return new ChatMessage(content, sender, timestamp, type);
            }
        }
    }

    public static class PrivateMessage extends ChatMessage {
        private String recipient;
        private String roomId;

        public PrivateMessage() {}

        public String getRecipient() { return recipient; }
        public void setRecipient(String recipient) { this.recipient = recipient; }
        
        public String getRoomId() { return roomId; }
        public void setRoomId(String roomId) { this.roomId = roomId; }
    }
    
    public static class TypingMessage {
        private String roomId;
        private String recipient;
        private boolean typing;
        
        public TypingMessage() {}
        
        public String getRoomId() { return roomId; }
        public void setRoomId(String roomId) { this.roomId = roomId; }
        
        public String getRecipient() { return recipient; }
        public void setRecipient(String recipient) { this.recipient = recipient; }
        
        public boolean isTyping() { return typing; }
        public void setTyping(boolean typing) { this.typing = typing; }
    }
    
    public static class ReadReceiptMessage {
        private String roomId;
        
        public ReadReceiptMessage() {}
        
        public String getRoomId() { return roomId; }
        public void setRoomId(String roomId) { this.roomId = roomId; }
    }
}
