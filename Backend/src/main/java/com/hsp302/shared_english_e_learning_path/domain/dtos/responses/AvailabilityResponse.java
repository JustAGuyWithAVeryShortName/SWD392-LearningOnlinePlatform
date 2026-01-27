package com.hsp302.shared_english_e_learning_path.domain.dtos.responses;

import com.hsp302.shared_english_e_learning_path.domain.enums.AppointmentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;



import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AvailabilityResponse {

    UUID availabilityID;
    AppointmentStatus status;
    List<String> appointmentDateTime;
    String createdAt;
    String updatedAt;
    UserResponse consultant;
}
