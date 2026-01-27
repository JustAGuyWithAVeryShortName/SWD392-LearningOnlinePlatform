package com.hsp302.shared_english_e_learning_path.domain.dtos.requests;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEnrollmentRequest {

    @NotNull(message = "Course ID must not be null")
    UUID courseId;

    @FutureOrPresent(message = "Start date must be today or in the future")
    String startedAt;

    @FutureOrPresent(message = "End date must be today or in the future")
    String endedAt;
}
