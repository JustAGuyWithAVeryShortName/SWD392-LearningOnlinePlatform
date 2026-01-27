package com.hsp302.shared_english_e_learning_path.domain.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAppointmentRequest {

    String notes;

    @NotNull(message = "Appointment date and time is required")
    String appointmentDateTime;

    @NotBlank(message = "Consultant ID (username) is required")
    String consultantID;
}
