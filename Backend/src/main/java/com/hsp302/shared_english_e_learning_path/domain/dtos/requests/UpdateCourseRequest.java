package com.hsp302.shared_english_e_learning_path.domain.dtos.requests;

import com.hsp302.shared_english_e_learning_path.domain.enums.AgeGroup;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateCourseRequest {

    @NotNull(message = "Course ID is required")
    UUID courseID;

    @NotBlank(message = "Course name is required")
    @Size(max = 255, message = "Course name must be at most 255 characters")
    String courseName;

    @Positive(message = "Duration must be a positive number")
    Integer duration;

    @PositiveOrZero(message = "Quantity cannot be negative")
    Integer quantity;

    String image;

    @NotBlank(message = "Description is required")
    String description;

    @NotNull(message = "Age group is required")
    AgeGroup ageGroup;

    @PositiveOrZero(message = "Price cannot be negative")
    Long price;
}
