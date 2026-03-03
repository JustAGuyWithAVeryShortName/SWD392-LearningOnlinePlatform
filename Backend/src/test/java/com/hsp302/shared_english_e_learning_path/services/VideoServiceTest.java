package com.hsp302.shared_english_e_learning_path.services;

import com.hsp302.shared_english_e_learning_path.domain.entities.Lesson;
import com.hsp302.shared_english_e_learning_path.domain.entities.Video;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.VideoResponse;
import com.hsp302.shared_english_e_learning_path.exception.InvalidVideoException;
import com.hsp302.shared_english_e_learning_path.exception.ResourceNotFoundException;
import com.hsp302.shared_english_e_learning_path.mappers.VideoMapper;
import com.hsp302.shared_english_e_learning_path.repositories.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Video Service Tests")
class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private VideoMapper videoMapper;

    @InjectMocks
    private VideoService videoService;

    private UUID lessonId;
    private UUID videoId;
    private Lesson testLesson;
    private Video testVideo;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        lessonId = UUID.randomUUID();
        videoId = UUID.randomUUID();
        
        testLesson = new Lesson();
        testLesson.setLessonId(lessonId);
        testLesson.setLessonName("Test Lesson");
        
        testVideo = new Video();
        testVideo.setVideoId(videoId);
        testVideo.setTitle("Test Video");
        testVideo.setVideoUrl("http://localhost:8080/api/videos/stream/test.mp4");
        testVideo.setDuration(120);
        testVideo.setLesson(testLesson);
    }

    @Test
    @DisplayName("Should throw InvalidVideoException when file is null")
    void testUploadVideoWithNullFile() throws IOException {
        MultipartFile nullFile = null;
        
        assertThrows(InvalidVideoException.class, () -> {
            videoService.uploadVideo("Test Title", nullFile, testLesson);
        });
    }

    @Test
    @DisplayName("Should throw InvalidVideoException when title is empty")
    void testUploadVideoWithEmptyTitle() throws IOException {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.mp4",
                "video/mp4",
                "test content".getBytes()
        );
        
        assertThrows(InvalidVideoException.class, () -> {
            videoService.uploadVideo("", file, testLesson);
        });
    }

    @Test
    @DisplayName("Should successfully retrieve videos by lesson ID")
    void testGetVideosByLesson() {
        // Arrange
        List<Video> videos = new ArrayList<>();
        videos.add(testVideo);
        
        List<VideoResponse> expectedResponses = new ArrayList<>();
        VideoResponse response = VideoResponse.builder()
                .videoId(videoId)
                .title("Test Video")
                .duration(120)
                .build();
        expectedResponses.add(response);
        
        when(videoRepository.findByLesson_LessonId(lessonId)).thenReturn(videos);
        when(videoMapper.toDto(testVideo)).thenReturn(response);
        
        // Act
        List<VideoResponse> result = videoService.getVideosByLesson(lessonId);
        
        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Video", result.get(0).getTitle());
        verify(videoRepository, times(1)).findByLesson_LessonId(lessonId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent video")
    void testDeleteNonExistentVideo() {
        UUID nonExistentId = UUID.randomUUID();
        
        when(videoRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            videoService.deleteVideo(nonExistentId);
        });
        
        verify(videoRepository, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should return empty list when lesson has no videos")
    void testGetVideosByLessonWithNoVideos() {
        // Arrange
        when(videoRepository.findByLesson_LessonId(lessonId)).thenReturn(new ArrayList<>());
        
        // Act
        List<VideoResponse> result = videoService.getVideosByLesson(lessonId);
        
        // Assert
        assertEquals(0, result.size());
        verify(videoRepository, times(1)).findByLesson_LessonId(lessonId);
    }
}
