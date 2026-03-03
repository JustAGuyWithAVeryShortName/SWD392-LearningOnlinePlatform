package com.hsp302.shared_english_e_learning_path.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hsp302.shared_english_e_learning_path.domain.entities.Lesson;
import com.hsp302.shared_english_e_learning_path.domain.entities.Video;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.VideoResponse;
import com.hsp302.shared_english_e_learning_path.exception.InvalidVideoException;
import com.hsp302.shared_english_e_learning_path.mappers.VideoMapper;
import com.hsp302.shared_english_e_learning_path.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for uploading, managing videos to Cloudinary
 * Provides scalable, cloud-based video storage and delivery
 */
@Service
public class CloudinaryVideoService {

    private final VideoRepository videoRepository;
    private final VideoMapper videoMapper;
    private final Cloudinary cloudinary;

    @Value("${app.video.max-file-size:104857600}")  // 100MB default
    private long maxFileSize;

    @Value("${app.video.allowed-extensions:mp4,avi,mov,mkv,flv,webm}")
    private String allowedExtensions;

    public CloudinaryVideoService(VideoRepository videoRepository, VideoMapper videoMapper, Cloudinary cloudinary) {
        this.videoRepository = videoRepository;
        this.videoMapper = videoMapper;
        this.cloudinary = cloudinary;
    }

    /**
     * Upload video to Cloudinary
     * @param title Video title
     * @param file Video file
     * @param lesson Associated lesson
     * @return VideoResponse
     * @throws IOException if upload fails
     */
    public VideoResponse uploadVideoToCloudinary(String title, MultipartFile file, Lesson lesson) throws IOException {

        // Validation
        validateVideoFile(file);

        if (title == null || title.isBlank()) {
            throw new InvalidVideoException("Video title cannot be empty");
        }

        try {
            // Upload to Cloudinary with video resource type
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "resource_type", "video",
                    "folder", "learning-platform/videos",
                    "public_id", "video_" + UUID.randomUUID(),
                    "eager_async", true
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            String videoUrl = (String) uploadResult.get("secure_url");
            Integer duration = (Integer) uploadResult.get("duration");

            // Let JPA generate UUID automatically
            Video video = Video.builder()
                    .title(title)
                    .videoUrl(videoUrl)
                    .duration(duration != null ? duration : 0)
                    .lesson(lesson)
                    .build();

            Video savedVideo = videoRepository.save(video);

            return videoMapper.toDto(savedVideo);

        } catch (IOException e) {
            throw new InvalidVideoException("Failed to upload video to Cloudinary: " + e.getMessage(), e);
        }
    }

    public void deleteVideoFromCloudinary(String publicId) throws IOException {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "video"));
        } catch (Exception e) {
            throw new InvalidVideoException("Failed to delete video from Cloudinary: " + e.getMessage(), e);
        }
    }

    private void validateVideoFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidVideoException("Video file is required");
        }

        if (file.getSize() > maxFileSize) {
            throw new InvalidVideoException(
                    String.format("File size exceeds maximum limit: %d bytes", maxFileSize)
            );
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new InvalidVideoException("Invalid file name");
        }

        String fileExtension = getFileExtension(filename).toLowerCase();
        String[] extensions = allowedExtensions.split(",");

        boolean isAllowed = false;
        for (String ext : extensions) {
            if (fileExtension.equals(ext.trim())) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new InvalidVideoException(
                    String.format("File type not allowed. Allowed types: %s", allowedExtensions)
            );
        }
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf(".");
        if (lastDot > 0 && lastDot < filename.length() - 1) {
            return filename.substring(lastDot + 1);
        }
        return "";
    }
}
