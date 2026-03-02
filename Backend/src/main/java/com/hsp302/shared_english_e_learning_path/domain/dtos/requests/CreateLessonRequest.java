package com.hsp302.shared_english_e_learning_path.domain.dtos.requests;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateLessonRequest {

    @NotBlank(message = "Lesson name ID is required")
    @Size(max = 255)
    String lessonName;

    @NotEmpty(message = "Objective is required")
    String objective;

    @NotEmpty(message = "Content is required")
    String content;

    @NotEmpty(message = "Resource is required")
    String resource;

    @NotNull(message = "Module ID is required")
    UUID moduleID;

    List<QuizRequest> quizzes;
}
