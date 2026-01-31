package com.hsp302.shared_english_e_learning_path.domain.dtos.responses;

import com.hsp302.shared_english_e_learning_path.domain.enums.EnrollmentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnrollmentResponse {

    UUID enrollmentID;
    EnrollmentStatus status;
    String startedAt;
    String endedAt;
    CourseResponse course;
    UserResponse member;
}
