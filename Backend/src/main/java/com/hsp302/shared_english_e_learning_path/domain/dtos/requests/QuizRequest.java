package com.hsp302.shared_english_e_learning_path.domain.dtos.requests;
import com.hsp302.shared_english_e_learning_path.domain.enums.QuizType;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizRequest {

    private String question;

    private QuizType type;

    private List<QuizOptionRequest> options;
}