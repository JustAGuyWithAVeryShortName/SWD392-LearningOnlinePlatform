package com.hsp302.shared_english_e_learning_path.controllers;

import com.hsp302.shared_english_e_learning_path.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/videos")
@Tag(name = "Video Streaming", description = "APIs for streaming and downloading videos")
public class VideoStreamController {

    private final Path ROOT = Paths.get("uploads/videos");

    /**
     * Stream video file inline (for browser playback)
     */
    @GetMapping("/stream/{filename}")
    @Operation(summary = "Stream a video", 
               description = "Stream a video file for playback in browser")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Video stream"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Video file not found")
    })
    public ResponseEntity<Resource> streamVideo(@PathVariable String filename) {
        try {
            Path file = ROOT.resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists()) {
                throw new ResourceNotFoundException("Video file not found: " + filename);
            }

            String contentType = "video/mp4";
            if (filename.endsWith(".avi")) {
                contentType = "video/x-msvideo";
            } else if (filename.endsWith(".mov")) {
                contentType = "video/quicktime";
            } else if (filename.endsWith(".mkv")) {
                contentType = "video/x-matroska";
            } else if (filename.endsWith(".flv")) {
                contentType = "video/x-flv";
            } else if (filename.endsWith(".webm")) {
                contentType = "video/webm";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Invalid file path: " + filename);
        }
    }

    /**
     * Download video file as attachment
     */
    @GetMapping("/download/{filename}")
    @Operation(summary = "Download a video", 
               description = "Download a video file as an attachment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Video file"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Video file not found")
    })
    public ResponseEntity<Resource> downloadVideo(@PathVariable String filename) {
        try {
            Path file = ROOT.resolve(filename).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists()) {
                throw new ResourceNotFoundException("Video file not found: " + filename);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Invalid file path: " + filename);
        }
    }
}
