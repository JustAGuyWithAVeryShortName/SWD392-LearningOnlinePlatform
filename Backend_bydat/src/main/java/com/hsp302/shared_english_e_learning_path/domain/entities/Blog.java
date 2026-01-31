package com.hsp302.shared_english_e_learning_path.domain.entities;

import com.hsp302.shared_english_e_learning_path.domain.enums.AgeGroup;
import com.hsp302.shared_english_e_learning_path.domain.enums.BlogStatus;
import com.hsp302.shared_english_e_learning_path.domain.enums.BlogType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "blog_id")
    UUID blogID;
    String blogName;
    String image;
    String description;
    @Column(columnDefinition = "NVARCHAR(MAX)")
    String content;
    Integer readingTime;
    @Enumerated(EnumType.STRING)
    BlogType blogType;
    @Enumerated(EnumType.STRING)
    BlogStatus blogStatus;
    @Enumerated(EnumType.STRING)
    AgeGroup ageGroup;
    Instant createdAt;
    Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    User member;

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
