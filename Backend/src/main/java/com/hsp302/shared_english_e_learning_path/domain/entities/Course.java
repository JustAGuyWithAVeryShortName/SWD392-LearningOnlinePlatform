package com.hsp302.shared_english_e_learning_path.domain.entities;

import com.hsp302.shared_english_e_learning_path.domain.enums.AgeGroup;
import com.hsp302.shared_english_e_learning_path.domain.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Course {

    @Id
    @Column(name = "course_id")
    UUID courseID;

    @Column(columnDefinition = "NVARCHAR(255)")
    String courseName;
    Integer quantity;
    Integer duration;
    String image;
    Long price;

    @Column(columnDefinition = "NVARCHAR(2000)")
    String description;
    @Enumerated(EnumType.STRING)
    AgeGroup ageGroup;
    @Enumerated(EnumType.STRING)
    CourseStatus status;
    Instant createdAt;
    Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = true)
    User staff;

    @OneToMany(mappedBy = "course")
    List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "course")
    List<Module> modules = new ArrayList<>();

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
