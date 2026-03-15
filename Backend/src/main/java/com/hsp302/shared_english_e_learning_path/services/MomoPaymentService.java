package com.hsp302.shared_english_e_learning_path.services;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.MomoApiRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.MomoInitiateRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.MomoIpnRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.MomoQueryRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.MomoApiResponse;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.MomoInitiateResponse;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.MomoQueryResponse;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.PaymentResponse;
import com.hsp302.shared_english_e_learning_path.domain.entities.Course;
import com.hsp302.shared_english_e_learning_path.domain.entities.Enrollment;
import com.hsp302.shared_english_e_learning_path.domain.entities.Payment;
import com.hsp302.shared_english_e_learning_path.domain.entities.User;
import com.hsp302.shared_english_e_learning_path.domain.enums.CourseStatus;
import com.hsp302.shared_english_e_learning_path.domain.enums.EnrollmentStatus;
import com.hsp302.shared_english_e_learning_path.domain.enums.PaymentStatus;
import com.hsp302.shared_english_e_learning_path.exception.AlreadyRegisteredException;
import com.hsp302.shared_english_e_learning_path.mappers.CourseMapper;
import com.hsp302.shared_english_e_learning_path.mappers.UserMapper;
import com.hsp302.shared_english_e_learning_path.repositories.EnrollmentRepository;
import com.hsp302.shared_english_e_learning_path.repositories.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MomoPaymentService {

    @Value("${momo.partner-code}")
    private String partnerCode;

    @Value("${momo.access-key}")
    private String accessKey;

    @Value("${momo.secret-key}")
    private String secretKey;

    @Value("${momo.endpoint}")
    private String endpoint;

    @Value("${momo.redirect-url}")
    private String redirectUrl;

    @Value("${momo.ipn-url}")
    private String ipnUrl;

    private final PaymentRepository paymentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseService courseService;
    private final UserService userService;
    private final CourseMapper courseMapper;
    private final UserMapper userMapper;
    private final RestTemplateBuilder restTemplateBuilder;

    /**
     * Step 1 of the payment flow.
     * Called by the frontend to create a MoMo payment and obtain the payUrl.
     */
    @PreAuthorize("hasAnyRole('MEMBER', 'MANAGER')")
    @Transactional
    public MomoInitiateResponse initiatePayment(MomoInitiateRequest request) {
        String username = userService.getLoginUsername();
        User member = userService.getUserEntity(username);
        Course course = courseService.getCourseEntity(request.getCourseId());

        // Validate the course requires payment
        if (course.getPrice() == null || course.getPrice() <= 0) {
            throw new IllegalArgumentException("This course is free. Use the normal enrollment flow.");
        }
        if (course.getStatus() != CourseStatus.AVAILABLE) {
            throw new IllegalArgumentException("Course is not available for enrollment.");
        }

        // Prevent duplicate payment
        boolean alreadyPaid = paymentRepository.existsByMemberUsernameAndCourseCourseIDAndStatus(
                username, course.getCourseID(), PaymentStatus.SUCCESS);
        if (alreadyPaid) {
            throw new AlreadyRegisteredException("You have already paid for this course.");
        }

        // Build unique identifiers
        String orderId = UUID.randomUUID().toString();
        String requestId = orderId;
        String orderInfo = "Thanh toan khoa hoc: " + course.getCourseName();
        String extraData = "";
        String requestType = "captureWallet";

        // Compute HMAC-SHA256 signature (field order is alphabetical per MoMo spec)
        String rawSignature = "accessKey=" + accessKey
                + "&amount=" + course.getPrice()
                + "&extraData=" + extraData
                + "&ipnUrl=" + ipnUrl
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + partnerCode
                + "&redirectUrl=" + redirectUrl
                + "&requestId=" + requestId
                + "&requestType=" + requestType;

        String signature = hmacSHA256(rawSignature, secretKey);

        MomoApiRequest momoRequest = MomoApiRequest.builder()
                .partnerCode(partnerCode)
                .requestType(requestType)
                .ipnUrl(ipnUrl)
                .redirectUrl(redirectUrl)
                .orderId(orderId)
                .amount(course.getPrice())
                .lang("vi")
                .orderInfo(orderInfo)
                .requestId(requestId)
                .extraData(extraData)
                .orderGroupId("")
                .autoCapture(true)
                .signature(signature)
                .build();

        // Call MoMo payment creation API
        RestTemplate restTemplate = restTemplateBuilder.build();
        MomoApiResponse momoResponse;
        try {
            momoResponse = restTemplate.postForObject(endpoint, momoRequest, MomoApiResponse.class);
        } catch (Exception e) {
            log.error("Failed to call MoMo API", e);
            throw new RuntimeException("Failed to contact MoMo payment gateway. Please try again.");
        }

        // Persist the pending payment record
        Payment payment = Payment.builder()
                .orderId(orderId)
                .requestId(requestId)
                .amount(course.getPrice())
                .orderInfo(orderInfo)
                .status(PaymentStatus.PENDING)
                .member(member)
                .course(course)
                .build();
        paymentRepository.save(payment);

        log.info("MoMo payment initiated: orderId={}, user={}, course={}", orderId, username, course.getCourseID());

        return MomoInitiateResponse.builder()
                .orderId(orderId)
                .payUrl(momoResponse != null ? momoResponse.getPayUrl() : null)
                .resultCode(momoResponse != null ? momoResponse.getResultCode() : -1)
                .message(momoResponse != null ? momoResponse.getMessage() : "No response from MoMo")
                .build();
    }

    /**
     * Step 2 of the payment flow.
     * Called by MoMo's servers via IPN (Instant Payment Notification).
     * Verifies signature, updates payment status, and creates enrollment on
     * success.
     */
    @Transactional
    public void handleIpn(MomoIpnRequest ipn) {
        // Verify HMAC-SHA256 signature from MoMo (field order is alphabetical per MoMo
        // spec)
        String rawSignature = "accessKey=" + accessKey
                + "&amount=" + ipn.getAmount()
                + "&extraData=" + (ipn.getExtraData() != null ? ipn.getExtraData() : "")
                + "&message=" + ipn.getMessage()
                + "&orderId=" + ipn.getOrderId()
                + "&orderInfo=" + ipn.getOrderInfo()
                + "&orderType=" + ipn.getOrderType()
                + "&partnerCode=" + ipn.getPartnerCode()
                + "&payType=" + ipn.getPayType()
                + "&requestId=" + ipn.getRequestId()
                + "&responseTime=" + ipn.getResponseTime()
                + "&resultCode=" + ipn.getResultCode()
                + "&transId=" + ipn.getTransId();

        String expected = hmacSHA256(rawSignature, secretKey);
        if (!expected.equals(ipn.getSignature())) {
            log.warn("Invalid MoMo IPN signature for orderId={}", ipn.getOrderId());
            throw new SecurityException("Invalid MoMo IPN signature");
        }

        Payment payment = paymentRepository.findByOrderId(ipn.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Payment not found for orderId: " + ipn.getOrderId()));

        // Update payment record
        payment.setResultCode(ipn.getResultCode());
        payment.setMessage(ipn.getMessage());
        payment.setMomoTransId(ipn.getTransId() != null ? String.valueOf(ipn.getTransId()) : null);

        if (Integer.valueOf(0).equals(ipn.getResultCode())) {
            // Payment succeeded → activate enrollment
            payment.setStatus(PaymentStatus.SUCCESS);

            Enrollment enrollment = Enrollment.builder()
                    .member(payment.getMember())
                    .course(payment.getCourse())
                    .status(EnrollmentStatus.LEARNING)
                    .startedAt(Instant.now())
                    .endedAt(Instant.now().plus(14, ChronoUnit.DAYS))
                    .build();
            enrollmentRepository.save(enrollment);

            log.info("Enrollment created after MoMo payment success: orderId={}, user={}, course={}",
                    ipn.getOrderId(),
                    payment.getMember().getUsername(),
                    payment.getCourse().getCourseID());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            log.info("MoMo payment failed: orderId={}, resultCode={}, message={}",
                    ipn.getOrderId(), ipn.getResultCode(), ipn.getMessage());
        }

        paymentRepository.save(payment);
    }

    /**
     * Queries MoMo for the real-time status of a payment and updates the DB.
     * Called by the frontend after being redirected back from MoMo, as a
     * safety net in case the IPN was not delivered (e.g. ngrok tunnel down).
     */
    @PreAuthorize("hasAnyRole('MEMBER', 'MANAGER')")
    @Transactional
    public PaymentResponse checkAndUpdatePaymentStatus(String orderId) {
        String username = userService.getLoginUsername();

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found for orderId: " + orderId));

        // Security: ensure the payment belongs to the calling user
        if (!payment.getMember().getUsername().equals(username)) {
            throw new SecurityException("Access denied: payment does not belong to the current user");
        }

        // If already resolved, return immediately without calling MoMo
        if (payment.getStatus() != PaymentStatus.PENDING) {
            return toResponse(payment);
        }

        // Build query signature (alphabetical field order per MoMo spec)
        String requestId = UUID.randomUUID().toString();
        String rawSignature = "accessKey=" + accessKey
                + "&orderId=" + orderId
                + "&partnerCode=" + partnerCode
                + "&requestId=" + requestId;
        String signature = hmacSHA256(rawSignature, secretKey);

        MomoQueryRequest queryRequest = MomoQueryRequest.builder()
                .partnerCode(partnerCode)
                .requestId(requestId)
                .orderId(orderId)
                .lang("vi")
                .signature(signature)
                .build();

        String queryEndpoint = endpoint.replace("/create", "/query");
        RestTemplate restTemplate = restTemplateBuilder.build();
        MomoQueryResponse queryResponse;
        try {
            queryResponse = restTemplate.postForObject(queryEndpoint, queryRequest, MomoQueryResponse.class);
        } catch (Exception e) {
            log.error("Failed to query MoMo transaction status for orderId={}", orderId, e);
            throw new RuntimeException("Failed to query MoMo payment status. Please try again.");
        }

        if (queryResponse == null) {
            log.warn("Null response from MoMo query API for orderId={}", orderId);
            return toResponse(payment);
        }

        log.info("MoMo query result: orderId={}, resultCode={}, message={}",
                orderId, queryResponse.getResultCode(), queryResponse.getMessage());

        // Update the payment record from the query response
        payment.setResultCode(queryResponse.getResultCode());
        payment.setMessage(queryResponse.getMessage());
        payment.setMomoTransId(queryResponse.getTransId() != null ? String.valueOf(queryResponse.getTransId()) : null);

        if (Integer.valueOf(0).equals(queryResponse.getResultCode())) {
            payment.setStatus(PaymentStatus.SUCCESS);

            boolean alreadyEnrolled = enrollmentRepository
                    .existsByMemberUsernameAndCourseCourseID(
                            username, payment.getCourse().getCourseID());
            if (!alreadyEnrolled) {
                Enrollment enrollment = Enrollment.builder()
                        .member(payment.getMember())
                        .course(payment.getCourse())
                        .status(EnrollmentStatus.LEARNING)
                        .startedAt(Instant.now())
                        .endedAt(Instant.now().plus(14, ChronoUnit.DAYS))
                        .build();
                enrollmentRepository.save(enrollment);
                log.info("Enrollment created via status-check: orderId={}, user={}, course={}",
                        orderId, username, payment.getCourse().getCourseID());
            }
        } else if (queryResponse.getResultCode() != null && queryResponse.getResultCode() != 1000
                && queryResponse.getResultCode() != 7000
                && queryResponse.getResultCode() != 7002) {
            // resultCode 1000 = pending/processing, 7000/7002 = being processed
            // Any other non-zero code is a definitive failure
            payment.setStatus(PaymentStatus.FAILED);
        }

        paymentRepository.save(payment);
        return toResponse(payment);
    }

    /**
     * Returns all payments for the currently logged-in user.
     */
    @PreAuthorize("hasAnyRole('MEMBER', 'MANAGER')")
    public List<PaymentResponse> getMyPayments() {
        String username = userService.getLoginUsername();
        return paymentRepository.findByMemberUsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .paymentId(p.getPaymentId())
                .orderId(p.getOrderId())
                .amount(p.getAmount())
                .orderInfo(p.getOrderInfo())
                .status(p.getStatus())
                .momoTransId(p.getMomoTransId())
                .resultCode(p.getResultCode())
                .message(p.getMessage())
                .createdAt(p.getCreatedAt() != null ? p.getCreatedAt().toString() : null)
                .updatedAt(p.getUpdatedAt() != null ? p.getUpdatedAt().toString() : null)
                .course(p.getCourse() != null ? courseMapper.toDto(p.getCourse()) : null)
                .member(p.getMember() != null ? userMapper.toDto(p.getMember()) : null)
                .build();
    }

    private String hmacSHA256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1)
                    hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error computing HMAC-SHA256", e);
        }
    }
}
