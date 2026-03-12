package com.hsp302.shared_english_e_learning_path.domain.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Returned to the frontend after initiating a MoMo payment.
 * The frontend redirects the user to {@code payUrl}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MomoInitiateResponse {

    String orderId;
    String payUrl;
    String message;
    Integer resultCode;
}
