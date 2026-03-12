package com.hsp302.shared_english_e_learning_path.domain.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Raw response returned by MoMo's payment-creation API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MomoApiResponse {

    String partnerCode;
    String orderId;
    String requestId;
    Long amount;
    Long responseTime;
    String message;
    Integer resultCode;
    String payUrl;
    String shortLink;
}
