package com.hsp302.shared_english_e_learning_path.controllers;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.CreateModuleRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.DeleteModulesRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.UpdateModuleRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.ApiResponse;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.LessonResponse;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.ModuleResponse;
import com.hsp302.shared_english_e_learning_path.services.ExcelService;
import com.hsp302.shared_english_e_learning_path.services.LessonService;
import com.hsp302.shared_english_e_learning_path.services.ModuleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/module")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;
    private final LessonService lessonService;
    private final ExcelService excelService;

    @PostMapping
    public ResponseEntity<ApiResponse<ModuleResponse>> createModule(@Valid @RequestBody CreateModuleRequest request) {
        ModuleResponse response = moduleService.createModule(request);
        ApiResponse<ModuleResponse> apiResponse = ApiResponse.<ModuleResponse>builder()
                .data(response)
                .status(HttpStatus.CREATED.value())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ModuleResponse>> getModule(@PathVariable UUID id) {
        ModuleResponse response = moduleService.getModel(id);
        ApiResponse<ModuleResponse> apiResponse = ApiResponse.<ModuleResponse>builder()
                .data(response)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{moduleID}/lessons")
    public ResponseEntity<ApiResponse<List<LessonResponse>>> getAllLessonByModuleID(@PathVariable UUID moduleID) {
        List<LessonResponse> responses = lessonService.getLessonsForModule(moduleID);
        ApiResponse<List<LessonResponse>> apiResponse = ApiResponse.<List<LessonResponse>>builder()
                .data(responses)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping(value = "/import", consumes = "multipart/form-data")
    public ResponseEntity<String> importCourses(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty!");
        }
        excelService.importModulesFromExcel(file.getInputStream());
        return ResponseEntity.ok("Excel file data saved Modules into DB");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ModuleResponse>> updateModule(@PathVariable UUID id,
                                                                    @RequestBody UpdateModuleRequest request) {
        ModuleResponse response = moduleService.updateModule(id, request);
        ApiResponse<ModuleResponse> apiResponse = ApiResponse.<ModuleResponse>builder()
                .data(response)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{courseID}/unavailable")
    public ResponseEntity<ApiResponse<List<ModuleResponse>>> updateModulesStatus(@PathVariable UUID courseID,
                                                                                 @RequestBody DeleteModulesRequest request) {
        List<ModuleResponse> responses = moduleService.updateModulesStatus(courseID, request);
        ApiResponse<List<ModuleResponse>> apiResponse = ApiResponse.<List<ModuleResponse>>builder()
                .status(HttpStatus.OK.value())
                .data(responses)
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
