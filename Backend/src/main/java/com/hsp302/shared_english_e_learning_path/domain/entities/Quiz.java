package com.hsp302.shared_english_e_learning_path.domain.entities;

import com.hsp302.shared_english_e_learning_path.domain.enums.QuizType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quiz {

        @Id
        @GeneratedValue
        UUID quizId;

        String question;

        @Enumerated(EnumType.STRING)
        QuizType type;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "lesson_id")
        Lesson lesson;

        @OneToMany(
                mappedBy = "quiz",
                cascade = CascadeType.ALL,
                orphanRemoval = true,
                fetch = FetchType.LAZY
        )
        List<QuizOption> options;
    }

