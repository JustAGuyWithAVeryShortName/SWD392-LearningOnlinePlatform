package com.hsp302.shared_english_e_learning_path.domain.dtos.responses;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Raw response returned by MoMo's transaction status query API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MomoQueryResponse {

    String partnerCode;
    String requestId;
    String orderId;
    String extraData;
    Long amount;
    Long transId;
    String payType;
    Integer resultCode;
    String message;
    Long responseTime;
    String orderInfo;
    String orderType;
    String signature;
}
