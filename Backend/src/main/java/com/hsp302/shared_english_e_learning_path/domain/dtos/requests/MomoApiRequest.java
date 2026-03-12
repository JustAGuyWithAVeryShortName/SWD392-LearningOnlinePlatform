package com.hsp302.shared_english_e_learning_path.domain.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Payload sent to MoMo's payment creation API.
 * All fields must match MoMo v2 gateway spec exactly.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MomoApiRequest {

    String partnerCode;
    String requestType;
    String ipnUrl;
    String redirectUrl;
    String orderId;
    Long amount;
    String lang;
    String orderInfo;
    String requestId;
    String extraData;
    String orderGroupId;
    boolean autoCapture;
    String signature;
}
