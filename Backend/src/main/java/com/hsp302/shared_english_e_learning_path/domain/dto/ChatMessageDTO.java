package com.hsp302.shared_english_e_learning_path.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageDTO {
    private String messageId;
    private String roomId;
    private String senderUsername;
    private String recipientUsername;
    private String content;
    private String messageType;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
    private Boolean isDelivered;
    private Boolean isRead;
    
    public static ChatMessageDTO fromEntity(com.hsp302.shared_english_e_learning_path.domain.entities.ChatMessage message) {
        return ChatMessageDTO.builder()
            .messageId(message.getMessageId().toString())
            .roomId(message.getRoomId())
            .senderUsername(message.getSenderUsername())
            .recipientUsername(message.getRecipientUsername())
            .content(message.getContent())
            .messageType(message.getMessageType().name())
            .sentAt(message.getSentAt())
            .deliveredAt(message.getDeliveredAt())
            .readAt(message.getReadAt())
            .isDelivered(message.getIsDelivered())
            .isRead(message.getIsRead())
            .build();
    }
}
