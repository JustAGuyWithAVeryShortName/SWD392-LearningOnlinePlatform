package com.hsp302.shared_english_e_learning_path.domain.entities;

import com.hsp302.shared_english_e_learning_path.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payment")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    UUID paymentId;

    /** Unique order id we send to MoMo, used to correlate the IPN callback */
    @Column(nullable = false, unique = true)
    String orderId;

    /** requestId = orderId in our implementation */
    @Column(nullable = false)
    String requestId;

    Long amount;

    @Column(columnDefinition = "NVARCHAR(500)")
    String orderInfo;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    PaymentStatus status = PaymentStatus.PENDING;

    /** MoMo transaction id, populated from IPN */
    String momoTransId;

    /** MoMo result code (0 = success) */
    Integer resultCode;

    @Column(columnDefinition = "NVARCHAR(500)")
    String message;

    Instant createdAt;
    Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    User member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    Course course;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
