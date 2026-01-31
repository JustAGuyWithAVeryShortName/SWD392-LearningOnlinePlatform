package com.hsp302.shared_english_e_learning_path.domain.dtos.requests;

import com.hsp302.shared_english_e_learning_path.domain.enums.AppointmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateAppointmentRequest {

    @NotBlank(message = "Appointment notes is required")
    String notes;

    @NotNull(message = "Appointment status is required")
    AppointmentStatus status;

    @NotNull(message = "Appointment date and time is required")
    String appointmentDateTime;
}
