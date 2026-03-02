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
public class QuizOption {
    @Id
    @GeneratedValue
    UUID optionId;

    String content;
    Boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    Quiz quiz;
}
