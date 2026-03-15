package com.hsp302.shared_english_e_learning_path.domain.dtos.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Payload sent to MoMo's transaction query API.
 * See: https://developers.momo.vn/#/docs/en/aiov2/?id=transaction-status-query
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MomoQueryRequest {

    String partnerCode;
    String requestId;
    String orderId;
    String lang;
    String signature;
}
