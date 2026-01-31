package com.hsp302.shared_english_e_learning_path.domain.dtos.responses;

import com.hsp302.shared_english_e_learning_path.domain.enums.AppointmentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppointmentResponse {

    UUID appointmentID;
    String notes;
    String link;
    AppointmentStatus status;
    String appointmentDateTime;
    String createdAt;
    String updatedAt;
    UserResponse member;
    UserResponse consultant;
}
