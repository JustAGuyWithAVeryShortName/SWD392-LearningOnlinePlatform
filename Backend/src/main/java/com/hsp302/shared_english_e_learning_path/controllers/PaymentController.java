package com.hsp302.shared_english_e_learning_path.controllers;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.MomoInitiateRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.MomoIpnRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.ApiResponse;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.MomoInitiateResponse;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.PaymentResponse;
import com.hsp302.shared_english_e_learning_path.services.MomoPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final MomoPaymentService momoPaymentService;

    @Value("${momo.redirect-url}")
    private String frontendRedirectUrl;

    /**
     * Frontend calls this to start the MoMo payment flow.
     * Returns a payUrl the frontend should redirect the user to.
     */
    @PostMapping("/momo/initiate")
    public ResponseEntity<ApiResponse<MomoInitiateResponse>> initiatePayment(
            @Valid @RequestBody MomoInitiateRequest request) {

        MomoInitiateResponse response = momoPaymentService.initiatePayment(request);

        ApiResponse<MomoInitiateResponse> apiResponse = ApiResponse.<MomoInitiateResponse>builder()
                .status(HttpStatus.OK.value())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    /**
     * MoMo servers POST to this URL after a payment attempt.
     * Must respond quickly with HTTP 204 to acknowledge receipt.
     */
    @PostMapping("/momo/ipn")
    public ResponseEntity<Void> handleIpn(@RequestBody MomoIpnRequest ipn) {
        momoPaymentService.handleIpn(ipn);
        return ResponseEntity.noContent().build();
    }

    /**
     * MoMo redirects the user's browser here after payment.
     * We redirect to the frontend with the result parameters.
     */
    @GetMapping("/momo/callback")
    public ResponseEntity<Void> handleCallback(
            @RequestParam String orderId,
            @RequestParam Integer resultCode,
            @RequestParam(required = false) String message) throws IOException {

        String location = frontendRedirectUrl
                + "?orderId=" + orderId
                + "&resultCode=" + resultCode
                + (message != null ? "&message=" + message : "");

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", location)
                .build();
    }

    /**
     * Returns all MoMo payments for the currently logged-in user.
     */
    @GetMapping("/my-history")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getMyPayments() {
        List<PaymentResponse> responses = momoPaymentService.getMyPayments();
        ApiResponse<List<PaymentResponse>> apiResponse = ApiResponse.<List<PaymentResponse>>builder()
                .status(HttpStatus.OK.value())
                .data(responses)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
