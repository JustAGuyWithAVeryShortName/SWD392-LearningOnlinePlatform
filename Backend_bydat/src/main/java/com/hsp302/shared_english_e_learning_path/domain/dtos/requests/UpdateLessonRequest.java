package com.hsp302.shared_english_e_learning_path.domain.dtos.requests;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateLessonRequest {

    @NotBlank(message = "Lesson name ID is required")
    @Size(max = 255)
    String lessonName;

    @NotEmpty(message = "Objective is required")
    String objective;

    @NotEmpty(message = "Content is required")
    String content;

    @NotEmpty(message = "Resource is required")
    String resource;
}