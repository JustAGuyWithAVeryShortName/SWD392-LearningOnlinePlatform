package com.hsp302.shared_english_e_learning_path.controllers;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.CreateAppointmentRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.UpdateAppointmentRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.ApiResponse;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.AppointmentResponse;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.CheckResponse;
import com.hsp302.shared_english_e_learning_path.domain.enums.AppointmentStatus;
import com.hsp302.shared_english_e_learning_path.services.AppointmentService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponse>> createAppointment(@Valid @RequestBody CreateAppointmentRequest request) throws GeneralSecurityException, IOException {
        AppointmentResponse response = appointmentService.createAppointment(request);
        ApiResponse<AppointmentResponse> apiResponse = ApiResponse.<AppointmentResponse>builder()
                .status(HttpStatus.CREATED.value())
                .data(response)
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getAllAppointments() {
        List<AppointmentResponse> responses = appointmentService.getAllAppointments();
        ApiResponse<List<AppointmentResponse>> apiResponse = ApiResponse.<List<AppointmentResponse>>builder()
                .status(HttpStatus.OK.value())
                .data(responses)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/my-list/{username}")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getMemberAppointments(@PathVariable String username) {
        List<AppointmentResponse> responses = appointmentService.getMemberAppointments(username);
        ApiResponse<List<AppointmentResponse>> apiResponse = ApiResponse.<List<AppointmentResponse>>builder()
                .status(HttpStatus.OK.value())
                .data(responses)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/consultant-list/{username}")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getConsultantAppointments(@PathVariable String username) {
        List<AppointmentResponse> responses = appointmentService.getConsultantAppointments(username);
        ApiResponse<List<AppointmentResponse>> apiResponse = ApiResponse.<List<AppointmentResponse>>builder()
                .status(HttpStatus.OK.value())
                .data(responses)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getAppointment(@PathVariable UUID id) {
        AppointmentResponse response = appointmentService.getAppointment(id);
        ApiResponse<AppointmentResponse> apiResponse = ApiResponse.<AppointmentResponse>builder()
                .status(HttpStatus.OK.value())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateAppointment(@PathVariable UUID id,
                                                                              @Valid @RequestBody UpdateAppointmentRequest request) {
        AppointmentResponse response = appointmentService.updateAppointment(id, request);
        ApiResponse<AppointmentResponse> apiResponse = ApiResponse.<AppointmentResponse>builder()
                .status(HttpStatus.OK.value())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/today/member/{username}")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getMemberTodayAppointments(@PathVariable String username) {
        List<AppointmentResponse> response = appointmentService.getMemberTodayAppointments(username);
        ApiResponse<List<AppointmentResponse>> apiResponse = ApiResponse.<List<AppointmentResponse>>builder()
                .status(HttpStatus.OK.value())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/today/consultant/{username}")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getConsultantTodayAppointments(@PathVariable String username) {
        List<AppointmentResponse> response = appointmentService.getConsultantTodayAppointments(username);
        ApiResponse<List<AppointmentResponse>> apiResponse = ApiResponse.<List<AppointmentResponse>>builder()
                .status(HttpStatus.OK.value())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/count-appointments-members/consultant/{username}")
    public ResponseEntity<ApiResponse<CheckResponse>> countConsultantAppointments(@PathVariable String username) {
        long totalAppointments = appointmentService.countConsultantAppointments(username);
        long totalMembers = appointmentService.countTotalMembersOfConsultant(username);
        CheckResponse response = CheckResponse.builder()
                .totalConsultantAppointments(totalAppointments)
                .totalMembersOfConsultant(totalMembers)
                .build();
        ApiResponse<CheckResponse> apiResponse = ApiResponse.<CheckResponse>builder()
                .status(HttpStatus.OK.value())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/appointments/{status}")
    public ResponseEntity<ApiResponse<List<LocalDateTime>>> getMemberBookedAppointmentByStatus(String username,
                                                                                               String from, String to,
                                                                                               @PathVariable AppointmentStatus status) {
        List<LocalDateTime> responses = appointmentService.getMemberBookedAppointmentByStatus(username, from, to, status);
        ApiResponse<List<LocalDateTime>> apiResponse = ApiResponse.<List<LocalDateTime>>builder()
                .status(HttpStatus.OK.value())
                .data(responses)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/cancel/{status}")
    public ResponseEntity<ApiResponse<AppointmentResponse>> cancelBookedAppointment(@PathVariable AppointmentStatus status,
                                                                                    @Valid @RequestBody UpdateAppointmentRequest request) throws MessagingException {
        AppointmentResponse response = appointmentService.cancelMemberScheduledAppointment(status, request);
        ApiResponse<AppointmentResponse> apiResponse = ApiResponse.<AppointmentResponse>builder()
                .status(HttpStatus.OK.value())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    //ADMIN HOMEPAGE
    @GetMapping("/admin/stats/appointments")
    public ResponseEntity<Map<String, Object>> getAppointmentStats() {
        return ResponseEntity.ok(appointmentService.getAppointmentStats());
    }
}
