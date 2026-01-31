package com.hsp302.shared_english_e_learning_path.domain.dtos.requests;

import com.hsp302.shared_english_e_learning_path.domain.enums.CourseStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DeleteModulesRequest {

    @NotEmpty(message = "Module IDs must not be empty")
    List<UUID> moduleIds;

    @NotNull(message = "Status is required")
    CourseStatus status;
}
