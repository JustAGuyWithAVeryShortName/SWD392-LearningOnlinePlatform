package com.hsp302.shared_english_e_learning_path.repositories;

import com.hsp302.shared_english_e_learning_path.domain.entities.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
    
    Optional<ChatRoom> findByRoomName(String roomName);
    
    @Query("SELECT cr FROM ChatRoom cr WHERE (cr.memberUsername = :username OR cr.consultantUsername = :username) AND cr.isActive = true ORDER BY cr.lastMessageAt DESC")
    List<ChatRoom> findActiveRoomsByUser(@Param("username") String username);
    
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.memberUsername = :memberUsername AND cr.consultantUsername = :consultantUsername")
    Optional<ChatRoom> findByMemberAndConsultant(@Param("memberUsername") String memberUsername, @Param("consultantUsername") String consultantUsername);
    
    List<ChatRoom> findByMemberUsernameAndIsActiveTrue(String memberUsername);
    
    List<ChatRoom> findByConsultantUsernameAndIsActiveTrue(String consultantUsername);
}
