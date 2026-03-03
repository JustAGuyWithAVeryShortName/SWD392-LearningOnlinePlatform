package com.hsp302.shared_english_e_learning_path.controllers;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.CreateVideoRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.ApiResponse;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.VideoResponse;
import com.hsp302.shared_english_e_learning_path.domain.entities.Lesson;
import com.hsp302.shared_english_e_learning_path.exception.ResourceNotFoundException;
import com.hsp302.shared_english_e_learning_path.repositories.LessonRepository;
import com.hsp302.shared_english_e_learning_path.services.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
@Tag(name = "Video Management", description = "APIs for managing course videos")
public class VideoController {

    private final VideoService videoService;
    private final LessonRepository lessonRepository;

    public VideoController(VideoService videoService,
                           LessonRepository lessonRepository) {
        this.videoService = videoService;
        this.lessonRepository = lessonRepository;
    }

    /**
     * Upload a new video for a lesson
     */
    @PostMapping("/upload")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    @Operation(summary = "Upload a new video", 
               description = "Upload a video file for a specific lesson. STAFF and MANAGER can upload.",
               security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Video uploaded successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid video file"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Lesson not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ApiResponse<VideoResponse>> uploadVideo(
            @RequestParam String title,
            @RequestParam MultipartFile file,
            @RequestParam UUID lessonId
    ) {
        try {
            // Verify lesson exists
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with ID: " + lessonId));

            VideoResponse response = videoService.uploadVideo(title, file, lesson);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<VideoResponse>builder()
                            .status(HttpStatus.CREATED.value())
                            .message("Video uploaded successfully")
                            .data(response)
                            .build());

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<VideoResponse>builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message(e.getMessage())
                            .build());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<VideoResponse>builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error saving video file: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get all videos for a specific lesson
     */
    @GetMapping("/lesson/{lessonId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get videos by lesson", 
               description = "Retrieve all videos associated with a specific lesson. Any authenticated user can view.",
               security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Videos retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Lesson not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<ApiResponse<List<VideoResponse>>> getByLesson(@PathVariable UUID lessonId) {
        try {
            // Verify lesson exists
            lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with ID: " + lessonId));

            List<VideoResponse> videos = videoService.getVideosByLesson(lessonId);

            return ResponseEntity.ok()
                    .body(ApiResponse.<List<VideoResponse>>builder()
                            .status(HttpStatus.OK.value())
                            .message("Videos retrieved successfully")
                            .data(videos)
                            .build());

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<List<VideoResponse>>builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message(e.getMessage())
                            .build());
        }
    }

    /**
     * Delete a video by ID
     */
    @DeleteMapping("/{videoId}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER')")
    @Operation(summary = "Delete a video", 
               description = "Delete a video by its ID. STAFF and MANAGER can delete.",
               security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Video deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Video not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<ApiResponse<Void>> deleteVideo(@PathVariable UUID videoId) {
        try {
            videoService.deleteVideo(videoId);

            return ResponseEntity.ok()
                    .body(ApiResponse.<Void>builder()
                            .status(HttpStatus.OK.value())
                            .message("Video deleted successfully")
                            .build());

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<Void>builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message(e.getMessage())
                            .build());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .message("Error deleting video file: " + e.getMessage())
                            .build());
        }
    }
}