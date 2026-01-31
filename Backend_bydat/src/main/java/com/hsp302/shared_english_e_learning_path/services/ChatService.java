package com.hsp302.shared_english_e_learning_path.services;

import com.hsp302.shared_english_e_learning_path.domain.entities.ChatMessage;
import com.hsp302.shared_english_e_learning_path.domain.entities.ChatRoom;
import com.hsp302.shared_english_e_learning_path.domain.entities.User;
import com.hsp302.shared_english_e_learning_path.domain.enums.Role;
import com.hsp302.shared_english_e_learning_path.repositories.ChatMessageRepository;
import com.hsp302.shared_english_e_learning_path.repositories.ChatRoomRepository;
import com.hsp302.shared_english_e_learning_path.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * Create or get existing chat room between member and consultant
     */
    @Transactional
    public ChatRoom createOrGetChatRoom(String memberUsername, String consultantUsername) {
        log.info("=== CREATING OR GETTING CHAT ROOM ===");
        log.info("Member: {}", memberUsername);
        log.info("Consultant: {}", consultantUsername);
        
        // Check if room already exists with either order
        String roomName1 = memberUsername + "-" + consultantUsername;
        String roomName2 = consultantUsername + "-" + memberUsername;
        
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByRoomName(roomName1);
        if (!existingRoom.isPresent()) {
            existingRoom = chatRoomRepository.findByRoomName(roomName2);
        }
        
        if (existingRoom.isPresent()) {
            log.info("Found existing room: {}", existingRoom.get().getRoomName());
            return existingRoom.get();
        }
        
        // Validate users exist and have correct roles (optional validation)
        try {
            User member = userRepository.findByUsername(memberUsername).orElse(null);
            User consultant = userRepository.findByUsername(consultantUsername).orElse(null);
            
            if (member != null && consultant != null) {
                if (member.getRole() != Role.MEMBER && consultant.getRole() != Role.MEMBER) {
                    throw new RuntimeException("At least one user must be a member");
                }
                if (member.getRole() != Role.CONSULTANT && consultant.getRole() != Role.CONSULTANT) {
                    throw new RuntimeException("At least one user must be a consultant");
                }
            } else {
                log.warn("Could not validate user roles - proceeding anyway");
            }
        } catch (Exception e) {
            log.warn("User role validation failed: {} - proceeding anyway", e.getMessage());
        }
        
        // Create new room using consistent naming (member-consultant format)
        String finalRoomName = memberUsername.contains("member") || consultantUsername.contains("consult") ? 
                               memberUsername + "-" + consultantUsername : 
                               consultantUsername + "-" + memberUsername;
        
        ChatRoom newRoom = ChatRoom.builder()
            .roomName(finalRoomName)
            .memberUsername(memberUsername)
            .consultantUsername(consultantUsername)
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .build();
        
        ChatRoom savedRoom = chatRoomRepository.save(newRoom);
        log.info("Created new room: {}", savedRoom.getRoomName());
        return savedRoom;
    }
    
    /**
     * Send and persist chat message
     */
    @Transactional
    public ChatMessage sendMessage(String roomId, String senderUsername, String recipientUsername, String content, ChatMessage.MessageType messageType) {
        log.info("=== SENDING MESSAGE ===");
        log.info("Room ID: {}", roomId);
        log.info("Sender: {}", senderUsername);
        log.info("Recipient: {}", recipientUsername);
        log.info("Content: {}", content);
        
        // Validate room exists, if not create it
        ChatRoom room = chatRoomRepository.findByRoomName(roomId).orElse(null);
        if (room == null) {
            log.warn("Chat room not found: {}, attempting to create it", roomId);
            // Try to create the room automatically
            try {
                room = createOrGetChatRoom(senderUsername.contains("member") ? senderUsername : recipientUsername,
                                         senderUsername.contains("consult") ? senderUsername : recipientUsername);
                log.info("Created new chat room: {}", room.getRoomName());
            } catch (Exception e) {
                log.error("Failed to create chat room: {}", e.getMessage());
                throw new RuntimeException("Chat room not found and could not be created: " + roomId);
            }
        }
        
        // Create and save message
        ChatMessage message = ChatMessage.builder()
            .roomId(roomId)
            .senderUsername(senderUsername)
            .recipientUsername(recipientUsername)
            .content(content)
            .messageType(messageType)
            .sentAt(LocalDateTime.now())
            .isDelivered(false)
            .isRead(false)
            .build();
        
        log.info("Saving message to database...");
        ChatMessage savedMessage = chatMessageRepository.save(message);
        log.info("Message saved with ID: {}", savedMessage.getMessageId());
        
        // Update room's last message time
        room.setLastMessageAt(LocalDateTime.now());
        chatRoomRepository.save(room);
        log.info("Updated room last message time");
        
        // Send via WebSocket
        log.info("Sending WebSocket message to user: {}", recipientUsername);
        com.hsp302.shared_english_e_learning_path.domain.dto.ChatMessageDTO messageDTO = 
            com.hsp302.shared_english_e_learning_path.domain.dto.ChatMessageDTO.fromEntity(savedMessage);
        messagingTemplate.convertAndSendToUser(recipientUsername, "/queue/private", messageDTO);
        
        log.info("Message processing completed successfully");
        return savedMessage;
    }
    
    /**
     * Get chat history for a room with pagination
     */
    public List<ChatMessage> getChatHistory(String roomId, int page, int size) {
        return chatMessageRepository.findByRoomIdOrderBySentAtDesc(roomId)
                .stream()
                .skip(page * size)
                .limit(size)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Get chat history for a room (backward compatibility)
     */
    public List<ChatMessage> getChatHistory(String roomId) {
        return chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId);
    }
    
    /**
     * Get new messages after a specific message ID
     */
    public List<ChatMessage> getChatHistoryAfterMessage(String roomId, String messageId) {
        try {
            java.util.UUID uuid = java.util.UUID.fromString(messageId);
            return chatMessageRepository.findByRoomIdAndMessageIdAfterOrderBySentAtAsc(roomId, uuid);
        } catch (IllegalArgumentException e) {
            // If invalid UUID, return empty list
            return new java.util.ArrayList<>();
        }
    }
    
    /**
     * Mark message as delivered
     */
    @Transactional
    public void markAsDelivered(String messageId) {
        chatMessageRepository.findById(java.util.UUID.fromString(messageId))
            .ifPresent(message -> {
                message.setIsDelivered(true);
                message.setDeliveredAt(LocalDateTime.now());
                chatMessageRepository.save(message);
            });
    }
    
    /**
     * Mark messages as read
     */
    @Transactional
    public void markMessagesAsRead(String roomId, String readerUsername) {
        List<ChatMessage> unreadMessages = chatMessageRepository.findUnreadMessagesInRoom(roomId, readerUsername);
        unreadMessages.forEach(message -> {
            message.setIsRead(true);
            message.setReadAt(LocalDateTime.now());
        });
        chatMessageRepository.saveAll(unreadMessages);
        
        // Notify sender about read receipt
        unreadMessages.forEach(message -> {
            messagingTemplate.convertAndSendToUser(message.getSenderUsername(), "/queue/read-receipt", 
                new ReadReceiptNotification(message.getMessageId().toString(), readerUsername, LocalDateTime.now()));
        });
    }
    
    /**
     * Send typing indicator
     */
    public void sendTypingIndicator(String roomId, String senderUsername, String recipientUsername, boolean isTyping) {
        TypingIndicator indicator = new TypingIndicator(roomId, senderUsername, isTyping, LocalDateTime.now());
        messagingTemplate.convertAndSendToUser(recipientUsername, "/queue/typing", indicator);
    }
    
    /**
     * Get active chat rooms for a user
     */
    public List<ChatRoom> getActiveRooms(String username) {
        return chatRoomRepository.findActiveRoomsByUser(username);
    }
    
    /**
     * Get unread message count for a user
     */
    public long getUnreadMessageCount(String username) {
        return chatMessageRepository.findUnreadMessagesByRecipient(username).size();
    }
    
    /**
     * Test method to check database connectivity
     */
    public Map<String, Long> getDatabaseCounts() {
        return Map.of(
            "roomCount", chatRoomRepository.count(),
            "messageCount", chatMessageRepository.count()
        );
    }
    
    // DTOs for WebSocket notifications
    public static class TypingIndicator {
        private String roomId;
        private String senderUsername;
        private boolean isTyping;
        private LocalDateTime timestamp;
        
        public TypingIndicator(String roomId, String senderUsername, boolean isTyping, LocalDateTime timestamp) {
            this.roomId = roomId;
            this.senderUsername = senderUsername;
            this.isTyping = isTyping;
            this.timestamp = timestamp;
        }
        
        // Getters
        public String getRoomId() { return roomId; }
        public String getSenderUsername() { return senderUsername; }
        public boolean isTyping() { return isTyping; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    public static class ReadReceiptNotification {
        private String messageId;
        private String readerUsername;
        private LocalDateTime readAt;
        
        public ReadReceiptNotification(String messageId, String readerUsername, LocalDateTime readAt) {
            this.messageId = messageId;
            this.readerUsername = readerUsername;
            this.readAt = readAt;
        }
        
        // Getters
        public String getMessageId() { return messageId; }
        public String getReaderUsername() { return readerUsername; }
        public LocalDateTime getReadAt() { return readAt; }
    }
}
