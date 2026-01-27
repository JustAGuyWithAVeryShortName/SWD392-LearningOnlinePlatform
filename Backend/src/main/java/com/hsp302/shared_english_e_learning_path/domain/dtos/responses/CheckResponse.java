package com.hsp302.shared_english_e_learning_path.domain.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CheckResponse {
    boolean existed;
    double completion;
    long totalConsultantAppointments;
    long totalMembersOfConsultant;
}
