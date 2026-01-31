package com.hsp302.shared_english_e_learning_path.domain.dtos.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReportRequest {

    String filterType;
    String startedMonth;
    String endedMonth;
}
