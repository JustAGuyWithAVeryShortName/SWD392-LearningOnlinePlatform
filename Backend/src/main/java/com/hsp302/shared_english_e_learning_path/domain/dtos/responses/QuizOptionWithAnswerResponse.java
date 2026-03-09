package com.hsp302.shared_english_e_learning_path.domain.dtos.responses;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class QuizOptionWithAnswerResponse {
    UUID optionId;
    String content;
    Boolean isCorrect; // 👈 chỉ manager thấy
}