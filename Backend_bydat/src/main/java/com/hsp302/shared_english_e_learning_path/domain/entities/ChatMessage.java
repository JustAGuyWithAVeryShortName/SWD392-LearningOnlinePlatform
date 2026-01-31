package com.hsp302.shared_english_e_learning_path.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "message_id")
    UUID messageId;
    
    @Column(name = "room_id", nullable = false)
    String roomId; // Format: "member_username-consultant_username"
    
    @Column(name = "sender_username", nullable = false)
    String senderUsername;
    
    @Column(name = "recipient_username", nullable = false)
    String recipientUsername;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    String content;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    MessageType messageType;
    
    @Column(name = "sent_at", nullable = false)
    LocalDateTime sentAt;
    
    @Column(name = "delivered_at")
    LocalDateTime deliveredAt;
    
    @Column(name = "read_at")
    LocalDateTime readAt;
    
    @Column(name = "is_delivered", nullable = false)
    @Builder.Default
    Boolean isDelivered = false;
    
    @Column(name = "is_read", nullable = false)
    @Builder.Default
    Boolean isRead = false;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_username", referencedColumnName = "username", insertable = false, updatable = false)
    User sender;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_username", referencedColumnName = "username", insertable = false, updatable = false)
    User recipient;
    
    @PrePersist
    protected void onCreate() {
        if (sentAt == null) {
            sentAt = LocalDateTime.now();
        }
    }
    
    public enum MessageType {
        TEXT, IMAGE, FILE, TYPING_START, TYPING_STOP
    }
}
