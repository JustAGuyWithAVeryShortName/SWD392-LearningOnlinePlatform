package com.hsp302.shared_english_e_learning_path.domain.dtos.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QuizResultResponse {

    private int total;     // tổng số câu
    private int correct;   // số câu đúng
    private int score;     // điểm %
    private boolean passed;
}