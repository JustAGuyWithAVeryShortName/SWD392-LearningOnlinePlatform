package com.hsp302.shared_english_e_learning_path.repositories;

import com.hsp302.shared_english_e_learning_path.domain.entities.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    
    List<ChatMessage> findByRoomIdOrderBySentAtAsc(String roomId);
    
    List<ChatMessage> findByRoomIdOrderBySentAtDesc(String roomId);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.roomId = :roomId AND cm.sentAt >= :since ORDER BY cm.sentAt ASC")
    List<ChatMessage> findByRoomIdAndSentAtAfter(@Param("roomId") String roomId, @Param("since") LocalDateTime since);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.recipientUsername = :username AND cm.isRead = false")
    List<ChatMessage> findUnreadMessagesByRecipient(@Param("username") String username);
    
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.roomId = :roomId AND cm.recipientUsername = :username AND cm.isRead = false")
    Long countUnreadMessagesInRoom(@Param("roomId") String roomId, @Param("username") String username);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.roomId = :roomId AND cm.recipientUsername = :username AND cm.isRead = false")
    List<ChatMessage> findUnreadMessagesInRoom(@Param("roomId") String roomId, @Param("username") String username);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.roomId = :roomId AND cm.messageId > :messageId ORDER BY cm.sentAt ASC")
    List<ChatMessage> findByRoomIdAndMessageIdAfterOrderBySentAtAsc(@Param("roomId") String roomId, @Param("messageId") UUID messageId);
}
