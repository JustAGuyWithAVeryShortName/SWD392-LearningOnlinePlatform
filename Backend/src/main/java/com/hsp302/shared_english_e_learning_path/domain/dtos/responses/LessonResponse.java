package com.hsp302.shared_english_e_learning_path.domain.dtos.responses;

import com.hsp302.shared_english_e_learning_path.domain.enums.CourseStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LessonResponse {

    UUID lessonID;
    String lessonName;
    int duration;
    String objective;
    String content;
    String resource;
    CourseStatus status;
    String createdAt;
    String updatedAt;
    ModuleResponse module;
    List<QuizResponse> quizzes;
    boolean hasQuiz;

}