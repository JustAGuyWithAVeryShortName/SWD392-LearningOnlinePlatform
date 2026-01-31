package com.hsp302.shared_english_e_learning_path.domain.dtos.responses;

import com.hsp302.shared_english_e_learning_path.domain.enums.ProgressStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProgressResponse {

    UUID progressID;
    UUID lessonID;
    ProgressStatus status;
    String completedAt;
    EnrollmentResponse enrollment;
}
