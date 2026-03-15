package com.hsp302.shared_english_e_learning_path.repositories;

import com.hsp302.shared_english_e_learning_path.domain.entities.Payment;
import com.hsp302.shared_english_e_learning_path.domain.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByOrderId(String orderId);

    List<Payment> findByMemberUsernameOrderByCreatedAtDesc(String username);

    long countByStatus(PaymentStatus status);

    long countByStatusAndUpdatedAtBetween(PaymentStatus status, Instant start, Instant end);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status")
    Long sumAmountByStatus(@Param("status") PaymentStatus status);

    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Payment p
            WHERE p.status = :status
              AND p.updatedAt BETWEEN :start AND :end
            """)
    Long sumAmountByStatusAndUpdatedAtBetween(
            @Param("status") PaymentStatus status,
            @Param("start") Instant start,
            @Param("end") Instant end);

    boolean existsByMemberUsernameAndCourseCourseIDAndStatus(
            String username, UUID courseId, PaymentStatus status);
}
