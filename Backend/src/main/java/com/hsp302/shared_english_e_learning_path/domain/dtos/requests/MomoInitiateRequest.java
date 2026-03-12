package com.hsp302.shared_english_e_learning_path.domain.dtos.requests;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

/**
 * Sent by the frontend to initiate a MoMo payment for a course.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MomoInitiateRequest {

    @NotNull(message = "Course ID must not be null")
    UUID courseId;
}
