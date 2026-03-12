package com.hsp302.shared_english_e_learning_path.domain.dtos.responses;

import com.hsp302.shared_english_e_learning_path.domain.enums.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {

    UUID paymentId;
    String orderId;
    Long amount;
    String orderInfo;
    PaymentStatus status;
    String momoTransId;
    Integer resultCode;
    String message;
    String createdAt;
    String updatedAt;
    CourseResponse course;
    UserResponse member;
}
