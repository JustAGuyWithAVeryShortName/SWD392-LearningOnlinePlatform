package com.hsp302.shared_english_e_learning_path.domain.entities;

import com.hsp302.shared_english_e_learning_path.domain.enums.ProgressStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.Instant;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
    name = "Progress",
    uniqueConstraints = @UniqueConstraint(
        name = "UQ_Progress_Enrollment_Lesson",
        columnNames = {"enrollment_id", "lesson_id"}
    )
)
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "progress_id")
    UUID progressID;
    @Column(name = "lesson_id")
    UUID lessonID;
    @Enumerated(EnumType.STRING)
    ProgressStatus status;
    Instant startedAt;
    Instant completedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id")
    Enrollment enrollment;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.startedAt = now;
        this.completedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.completedAt = Instant.now();
    }
}
