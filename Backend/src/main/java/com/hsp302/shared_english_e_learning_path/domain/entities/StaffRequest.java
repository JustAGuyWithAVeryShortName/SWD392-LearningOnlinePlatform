package com.hsp302.shared_english_e_learning_path.domain.entities;

import com.hsp302.shared_english_e_learning_path.domain.enums.RequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffRequest {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    private User user;

    @Enumerated(EnumType.STRING)
    private RequestStatus status; // PENDING, APPROVED, REJECTED

    private String reason; // member ghi
    private String adminNote;

    private Instant createdAt;
    private Instant reviewedAt;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }
}