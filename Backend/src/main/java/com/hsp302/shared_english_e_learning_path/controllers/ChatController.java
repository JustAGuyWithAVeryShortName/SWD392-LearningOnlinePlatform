package com.hsp302.shared_english_e_learning_path.controllers;

import com.hsp302.shared_english_e_learning_path.domain.entities.ChatMessage;
import com.hsp302.shared_english_e_learning_path.domain.entities.ChatRoom;
import com.hsp302.shared_english_e_learning_path.services.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ChatController {
    
    private final ChatService chatService;
    
    /**
     * Get chat history for a room with pagination
     * Fixes the 404 error for /chat/history/{roomId}
     */
    @GetMapping("/history/{roomId}")
    public ResponseEntity<List<com.hsp302.shared_english_e_learning_path.domain.dto.ChatMessageDTO>> getChatHistory(
            @PathVariable String roomId, 
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String lastMessageId,
            Authentication authentication) {
        log.info("Getting chat history for room: {} by user: {} (page: {}, size: {})", 
                roomId, authentication.getName(), page, size);
        
        List<ChatMessage> messages;
        if (lastMessageId != null) {
            // Get messages after a specific message ID (for real-time updates)
            messages = chatService.getChatHistoryAfterMessage(roomId, lastMessageId);
        } else {
            // Get paginated history
            messages = chatService.getChatHistory(roomId, page, size);
        }
        
        // Convert to DTOs to avoid serialization issues
        List<com.hsp302.shared_english_e_learning_path.domain.dto.ChatMessageDTO> messageDTOs = messages.stream()
            .map(com.hsp302.shared_english_e_learning_path.domain.dto.ChatMessageDTO::fromEntity)
            .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(messageDTOs);
    }
    
    /**
     * Create or get chat room between member and consultant
     */
    @PostMapping("/room")
    public ResponseEntity<ChatRoom> createChatRoom(@RequestBody Map<String, String> request, Authentication authentication) {
        String memberUsername = request.get("memberUsername");
        String consultantUsername = request.get("consultantUsername");
        
        log.info("=== CREATING CHAT ROOM ===");
        log.info("Request from user: {}", authentication.getName());
        log.info("Member: {}", memberUsername);
        log.info("Consultant: {}", consultantUsername);
        
        try {
            ChatRoom room = chatService.createOrGetChatRoom(memberUsername, consultantUsername);
            log.info("Chat room created/retrieved successfully: {}", room.getRoomName());
            return ResponseEntity.ok(room);
        } catch (Exception e) {
            log.error("Error creating chat room: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Test endpoint to create a chat room manually
     */
    @PostMapping("/room/test")
    public ResponseEntity<Map<String, Object>> createTestChatRoom(@RequestParam String member, @RequestParam String consultant) {
        log.info("Creating test chat room between {} and {}", member, consultant);
        try {
            ChatRoom room = chatService.createOrGetChatRoom(member, consultant);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "room", room,
                "message", "Chat room created successfully"
            ));
        } catch (Exception e) {
            log.error("Failed to create test chat room: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    /**
     * Get active chat rooms for current user
     */
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoom>> getActiveRooms(Authentication authentication) {
        String username = authentication.getName();
        log.info("Getting active rooms for user: {}", username);
        List<ChatRoom> rooms = chatService.getActiveRooms(username);
        return ResponseEntity.ok(rooms);
    }
    


    
    /**
     * Mark messages as read
     */
    @PostMapping("/read/{roomId}")
    public ResponseEntity<Void> markAsRead(@PathVariable String roomId, Authentication authentication) {
        String username = authentication.getName();
        log.info("Marking messages as read in room {} by user {}", roomId, username);
        chatService.markMessagesAsRead(roomId, username);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get unread message count for current user
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        String username = authentication.getName();
        long count = chatService.getUnreadMessageCount(username);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }
    
    /**
     * Test endpoint to send message directly via REST API
     */
    @PostMapping("/test-message")
    public ResponseEntity<Map<String, Object>> testSendMessage(
            @RequestParam String roomId,
            @RequestParam String sender,
            @RequestParam String recipient,
            @RequestParam String content) {
        
        log.info("=== TEST MESSAGE ENDPOINT ===");
        log.info("Room: {}, Sender: {}, Recipient: {}, Content: {}", roomId, sender, recipient, content);
        
        try {
            // First, try to create/get the chat room
            chatService.createOrGetChatRoom(
                sender.contains("member") ? sender : recipient,
                sender.contains("consult") ? sender : recipient
            );
            
            // Then send the message
            com.hsp302.shared_english_e_learning_path.domain.entities.ChatMessage savedMessage = 
                chatService.sendMessage(roomId, sender, recipient, content, 
                    com.hsp302.shared_english_e_learning_path.domain.entities.ChatMessage.MessageType.TEXT);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "messageId", savedMessage.getMessageId().toString(),
                "message", "Message saved successfully"
            ));
        } catch (Exception e) {
            log.error("Test message failed: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", e.getMessage(),
                "stackTrace", java.util.Arrays.toString(e.getStackTrace())
            ));
        }
    }
    
    /**
     * Test endpoint to check database tables
     */
    @GetMapping("/test-db")
    public ResponseEntity<Map<String, Object>> testDatabase() {
        try {
            Map<String, Long> counts = chatService.getDatabaseCounts();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "roomCount", counts.get("roomCount"),
                "messageCount", counts.get("messageCount"),
                "message", "Database tables accessible"
            ));
        } catch (Exception e) {
            log.error("Database test failed: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
}
