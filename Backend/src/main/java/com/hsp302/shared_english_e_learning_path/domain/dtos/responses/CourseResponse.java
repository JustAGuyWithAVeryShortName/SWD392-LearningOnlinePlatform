package com.hsp302.shared_english_e_learning_path.domain.dtos.responses;

import com.hsp302.shared_english_e_learning_path.domain.enums.AgeGroup;
import com.hsp302.shared_english_e_learning_path.domain.enums.CourseStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseResponse {

    UUID courseID;
    String courseName;
    Integer quantity;
    Integer duration;
    String image;
    String description;
    AgeGroup ageGroup;
    CourseStatus status;
    String createdAt;
    String updatedAt;
    private String staffUsername;
    private String staffFullName;
}
