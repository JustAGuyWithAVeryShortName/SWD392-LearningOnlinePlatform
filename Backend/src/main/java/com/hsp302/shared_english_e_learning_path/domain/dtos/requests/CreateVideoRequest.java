package com.hsp302.shared_english_e_learning_path.domain.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateVideoRequest {

    @NotBlank(message = "Video title is required")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    String title;

    @NotNull(message = "Video file is required")
    MultipartFile file;

    @NotNull(message = "Lesson ID is required")
    UUID lessonId;
}
