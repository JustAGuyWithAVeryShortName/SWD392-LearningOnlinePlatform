package com.hsp302.shared_english_e_learning_path.repositories;

import com.hsp302.shared_english_e_learning_path.domain.entities.Payment;
import com.hsp302.shared_english_e_learning_path.domain.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByOrderId(String orderId);

    List<Payment> findByMemberUsernameOrderByCreatedAtDesc(String username);

    boolean existsByMemberUsernameAndCourseCourseIDAndStatus(
            String username, UUID courseId, PaymentStatus status);
}
