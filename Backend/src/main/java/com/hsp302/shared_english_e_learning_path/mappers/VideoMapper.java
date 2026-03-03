package com.hsp302.shared_english_e_learning_path.mappers;

import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.VideoResponse;
import com.hsp302.shared_english_e_learning_path.domain.entities.Video;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VideoMapper {

    default VideoResponse toDto(Video video) {
        if (video == null) {
            return null;
        }
        
        return VideoResponse.builder()
                .videoId(video.getVideoId())
                .title(video.getTitle())
                .videoUrl(video.getVideoUrl())
                .duration(video.getDuration())
                .lessonId(video.getLesson() != null ? video.getLesson().getLessonID() : null)
                .lessonName(video.getLesson() != null ? video.getLesson().getLessonName() : null)
                .build();
    }
}
