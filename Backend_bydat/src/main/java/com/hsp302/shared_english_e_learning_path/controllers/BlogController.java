package com.hsp302.shared_english_e_learning_path.controllers;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.CreateBlogRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.UpdateBlogRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.ApiResponse;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.BlogResponse;
import com.hsp302.shared_english_e_learning_path.domain.enums.AgeGroup;
import com.hsp302.shared_english_e_learning_path.domain.enums.BlogStatus;
import com.hsp302.shared_english_e_learning_path.domain.enums.BlogType;
import com.hsp302.shared_english_e_learning_path.domain.enums.Role;
import com.hsp302.shared_english_e_learning_path.services.BlogService;
import com.hsp302.shared_english_e_learning_path.services.ExcelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/blog")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;
    private final ExcelService excelService;

    @PostMapping
    public ResponseEntity<ApiResponse<BlogResponse>> createBlog(@Valid @RequestBody CreateBlogRequest request) {
        BlogResponse response = blogService.createBlog(request);
        ApiResponse<BlogResponse> apiResponse = ApiResponse.<BlogResponse>builder()
                .data(response)
                .status(HttpStatus.CREATED.value())
                .build();
        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BlogResponse>>> getBlogs() {
        List<BlogResponse> responses = blogService.getAllBlogs();
        ApiResponse<List<BlogResponse>> apiResponses = ApiResponse.<List<BlogResponse>>builder()
                .data(responses)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BlogResponse>> getBlog(@PathVariable UUID id) {
        BlogResponse response = blogService.getBlog(id);
        ApiResponse<BlogResponse> apiResponse = ApiResponse.<BlogResponse>builder()
                .data(response)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BlogResponse>> updateBlog(@PathVariable UUID id,
                                                                @Valid @RequestBody UpdateBlogRequest request) {
        BlogResponse response = blogService.updateBlog(id, request);
        ApiResponse<BlogResponse> apiResponse = ApiResponse.<BlogResponse>builder()
                .data(response)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}/{status}")
    public ResponseEntity<ApiResponse<BlogResponse>> updateBlogStatus(@PathVariable UUID id,
                                                                      @PathVariable BlogStatus status) {
        BlogResponse response = blogService.updateBlogStatus(id, status);
        ApiResponse<BlogResponse> apiResponse = ApiResponse.<BlogResponse>builder()
                .data(response)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping(value = "/import", consumes = "multipart/form-data")
    public ResponseEntity<String> importUserDetails(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty!");
        }
        excelService.importBlogsFromExcel(file.getInputStream());
        return ResponseEntity.ok("Excel file data saved Blogs into DB");
    }

    @GetMapping("/type")
    public ResponseEntity<ApiResponse<List<String>>> getAllBlogTypes() {
        List<String> types = Arrays.stream(BlogType.values())
                .map(Enum::name)
                .toList();
        ApiResponse<List<String>> apiResponse = ApiResponse.<List<String>>builder()
                .status(HttpStatus.OK.value())
                .data(types)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<String>>> getAllBlogStatuses() {
        List<String> statuses = Arrays.stream(BlogStatus.values())
                .map(Enum::name)
                .toList();
        ApiResponse<List<String>> apiResponse = ApiResponse.<List<String>>builder()
                .status(HttpStatus.OK.value())
                .data(statuses)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/age-group/{ageGroup}")
    public ResponseEntity<ApiResponse<List<BlogResponse>>> getBlogsByAgeGroup(@PathVariable AgeGroup ageGroup) {
        List<BlogResponse> responses = blogService.getBlogsByAgeGroup(ageGroup);
        ApiResponse<List<BlogResponse>> apiResponses = ApiResponse.<List<BlogResponse>>builder()
                .data(responses)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponses);
    }

    @GetMapping("/my-list/{username}/status/{status}")
    public ResponseEntity<ApiResponse<List<BlogResponse>>> getMyBlogsByStatus(@PathVariable String username,
                                                                              @PathVariable BlogStatus status) {
        List<BlogResponse> responses = blogService.getMyBlogsByStatus(username, status);
        ApiResponse<List<BlogResponse>> apiResponses = ApiResponse.<List<BlogResponse>>builder()
                .data(responses)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponses);
    }

    @GetMapping("/status/{status}/role-except/{role}")
    public ResponseEntity<ApiResponse<List<BlogResponse>>> getBlogsByStatusExceptRole(@PathVariable BlogStatus status,
                                                                                      @PathVariable Role role) {
        List<BlogResponse> responses = blogService.getBlogsByStatusExceptRole(status, role);
        ApiResponse<List<BlogResponse>> apiResponses = ApiResponse.<List<BlogResponse>>builder()
                .data(responses)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponses);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<List<BlogResponse>>> getBlogsByRole(@PathVariable Role role) {
        List<BlogResponse> responses = blogService.getBlogsByRole(role);
        ApiResponse<List<BlogResponse>> apiResponses = ApiResponse.<List<BlogResponse>>builder()
                .data(responses)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponses);
    }

    @GetMapping("/status/{status}/role/{role}")
    public ResponseEntity<ApiResponse<List<BlogResponse>>> getBlogsByStatusRole(@PathVariable BlogStatus status,
                                                                                @PathVariable Role role) {
        List<BlogResponse> responses = blogService.getBlogsByStatusAndRole(status, role);
        ApiResponse<List<BlogResponse>> apiResponses = ApiResponse.<List<BlogResponse>>builder()
                .data(responses)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponses);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<BlogResponse>>> getBlogsByStatus(@PathVariable BlogStatus status) {
        List<BlogResponse> responses = blogService.getBlogsByStatus(status);
        ApiResponse<List<BlogResponse>> apiResponses = ApiResponse.<List<BlogResponse>>builder()
                .data(responses)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponses);
    }

    //ADMIN HOMEPAGE
    @GetMapping("/admin/stats/blogs")
    public ResponseEntity<Map<String, Object>> getBlogStats() {
        return ResponseEntity.ok(blogService.getBlogStats());
    }

}
