package com.hsp302.shared_english_e_learning_path.domain.dtos.responses;

import com.hsp302.shared_english_e_learning_path.domain.enums.CourseStatus;
import com.hsp302.shared_english_e_learning_path.domain.enums.Degree;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QualificationResponse {

    UUID qualificationID;
    String name;
    String image;
    Degree degree;
    String institution;
    Integer year;
    CourseStatus status;
    String createdAt;
    String updatedAt;
    UserResponse consultant;
}
