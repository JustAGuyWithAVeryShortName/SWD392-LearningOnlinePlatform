package com.hsp302.shared_english_e_learning_path.controllers;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.ReportRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.ApiResponse;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.ReportResponse;
import com.hsp302.shared_english_e_learning_path.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ApiResponse<List<ReportResponse>>> getLineChartData(@RequestBody ReportRequest request) {
        List<ReportResponse> responses = reportService.getLineChartData(request);
        ApiResponse<List<ReportResponse>> apiResponse = ApiResponse.<List<ReportResponse>>builder()
                .status(HttpStatus.OK.value())
                .data(responses)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ReportResponse>> getStatCardData() {
        ReportResponse response = reportService.getStatCardData();
        ApiResponse<ReportResponse> apiResponse = ApiResponse.<ReportResponse>builder()
                .status(HttpStatus.OK.value())
                .data(response)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
