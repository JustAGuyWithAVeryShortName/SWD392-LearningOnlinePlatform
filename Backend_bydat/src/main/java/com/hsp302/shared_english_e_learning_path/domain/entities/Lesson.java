package com.hsp302.shared_english_e_learning_path.domain.entities;

import com.hsp302.shared_english_e_learning_path.domain.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Lesson {

    @Id
    @Column(name = "lesson_id")
    UUID lessonID;
    String lessonName;
    int duration;
    String objective;
    @Column(columnDefinition = "NVARCHAR(MAX)")
    String content;
    String resource;
    @Enumerated(EnumType.STRING)
    CourseStatus status;
    Instant createdAt;
    Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    Module module;

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
