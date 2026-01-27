package com.hsp302.shared_english_e_learning_path.domain.dtos.requests;

import com.hsp302.shared_english_e_learning_path.domain.enums.Degree;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateQualificationRequest {

    @NotBlank(message = "Name is required")
    String name;

    String image;

    @NotNull(message = "Degree is required")
    Degree degree;

    @NotBlank(message = "Institution is required")
    String institution;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be valid")
    Integer year;
}
