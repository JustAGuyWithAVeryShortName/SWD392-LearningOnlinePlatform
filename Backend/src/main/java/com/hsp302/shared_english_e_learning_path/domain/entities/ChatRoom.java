package com.hsp302.shared_english_e_learning_path.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chat_rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatRoom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "room_id")
    UUID roomId;
    
    @Column(name = "room_name", nullable = false, unique = true)
    String roomName; // Format: "member_username-consultant_username"
    
    @Column(name = "member_username", nullable = false)
    String memberUsername;
    
    @Column(name = "consultant_username", nullable = false)
    String consultantUsername;
    
    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;
    
    @Column(name = "last_message_at")
    LocalDateTime lastMessageAt;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    Boolean isActive = true;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_username", referencedColumnName = "username", insertable = false, updatable = false)
    User member;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultant_username", referencedColumnName = "username", insertable = false, updatable = false)
    User consultant;
    
    @JsonIgnore
    @OneToMany(mappedBy = "roomId", fetch = FetchType.LAZY)
    @Builder.Default
    List<ChatMessage> messages = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (lastMessageAt == null) {
            lastMessageAt = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastMessageAt = LocalDateTime.now();
    }
}
