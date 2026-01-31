package com.hsp302.shared_english_e_learning_path.domain.dtos.requests;

import com.hsp302.shared_english_e_learning_path.domain.enums.AgeGroup;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SaveAsDraftRequest {

    // eventName thường là bắt buộc ngay cả với draft để định danh
    @NotBlank(message = "Event name must not be blank.")
    String eventName;

    String subTitle;
    Integer duration;
    Integer quantity;
    String description;
    String image;
    AgeGroup ageGroup;
    LocalDateTime startDate;
    LocalDateTime endDate;
    String location;
    Double fee;
    String details;
}
