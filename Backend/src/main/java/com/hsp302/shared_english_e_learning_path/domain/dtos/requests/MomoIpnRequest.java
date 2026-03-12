package com.hsp302.shared_english_e_learning_path.domain.dtos.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * IPN (Instant Payment Notification) callback sent by MoMo to our server
 * after a payment attempt (success or failure).
 *
 * MoMo sends this as a POST with JSON body to the {@code ipnUrl} we provided.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MomoIpnRequest {

    String partnerCode;
    String orderId;
    String requestId;
    Long amount;
    String orderInfo;
    String orderType;
    Long transId;
    Integer resultCode;
    String message;
    String payType;
    Long responseTime;
    String extraData;
    String signature;
}
