package com.hsp302.shared_english_e_learning_path.domain.dtos.requests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizOptionRequest {

    private String content;

    private Boolean isCorrect;
}