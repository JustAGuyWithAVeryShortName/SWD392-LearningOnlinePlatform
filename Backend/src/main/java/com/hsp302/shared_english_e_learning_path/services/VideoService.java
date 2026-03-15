package com.hsp302.shared_english_e_learning_path.services;

import com.hsp302.shared_english_e_learning_path.domain.entities.Lesson;
import com.hsp302.shared_english_e_learning_path.domain.entities.Video;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.VideoResponse;
import com.hsp302.shared_english_e_learning_path.exception.InvalidVideoException;
import com.hsp302.shared_english_e_learning_path.mappers.VideoMapper;
import com.hsp302.shared_english_e_learning_path.repositories.VideoRepository;
import com.hsp302.shared_english_e_learning_path.utils.VideoMetadataUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class VideoService {

    private final VideoRepository videoRepository;
    private final VideoMapper videoMapper;
    private final CourseService courseService;
    private final Path ROOT = Paths.get("uploads/videos");

    @Value("${app.video.max-file-size:104857600}") // 100MB default
    private long maxFileSize;

    @Value("${app.video.allowed-extensions:mp4,avi,mov,mkv,flv}")
    private String allowedExtensions;

    @Value("${app.video.base-url:http://localhost:8080}")
    private String baseUrl;

    public VideoService(VideoRepository videoRepository, VideoMapper videoMapper, CourseService courseService) {
        this.videoRepository = videoRepository;
        this.videoMapper = videoMapper;
        this.courseService = courseService;
    }

    public VideoResponse uploadVideo(String title, MultipartFile file, Lesson lesson) throws IOException {

        // Validation
        validateVideoFile(file);

        if (title == null || title.isBlank()) {
            throw new InvalidVideoException("Video title cannot be empty");
        }

        Files.createDirectories(ROOT);

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = ROOT.resolve(filename);

        Files.copy(file.getInputStream(), filePath);

        // Extract video duration
        int duration = VideoMetadataUtil.extractDuration(filePath.toFile());

        // Let JPA generate UUID automatically
        Video video = Video.builder()
                .title(title)
                .videoUrl(baseUrl + "/api/videos/stream/" + filename)
                .duration(duration)
                .lesson(lesson)
                .build();

        Video savedVideo = videoRepository.save(video);
        courseService.refreshCourseDuration(lesson.getModule().getCourse().getCourseID());

        return videoMapper.toDto(savedVideo);
    }

    public List<VideoResponse> getVideosByLesson(UUID lessonId) {
        List<Video> videos = videoRepository.findByLesson_LessonID(lessonId);
        return videos.stream()
                .map(videoMapper::toDto)
                .toList();
    }

    public void deleteVideo(UUID videoId) throws IOException {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new com.hsp302.shared_english_e_learning_path.exception.ResourceNotFoundException(
                        "Video not found with ID: " + videoId));
        UUID courseId = video.getLesson().getModule().getCourse().getCourseID();

        // Extract filename from URL
        String videoUrl = video.getVideoUrl();
        String filename = videoUrl.substring(videoUrl.lastIndexOf("/") + 1);

        // Delete file from filesystem
        Path filePath = ROOT.resolve(filename);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        // Delete record from database
        videoRepository.deleteById(videoId);
        courseService.refreshCourseDuration(courseId);
    }

    private void validateVideoFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidVideoException("Video file is required");
        }

        if (file.getSize() > maxFileSize) {
            throw new InvalidVideoException(
                    String.format("File size exceeds maximum limit: %d bytes", maxFileSize));
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
                    String.format("File type not allowed. Allowed types: %s", allowedExtensions));
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