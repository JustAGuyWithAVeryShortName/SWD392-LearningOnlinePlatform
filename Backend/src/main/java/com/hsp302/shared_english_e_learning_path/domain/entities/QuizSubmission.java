package com.hsp302.shared_english_e_learning_path.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"username", "quiz_id"})
        }
)
public class QuizSubmission {

    @Id
    UUID submissionId;

    @Column(name = "username", nullable = false)
    String username;

    @Column(name = "is_correct", nullable = false)
    Boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    Quiz quiz;
}