package com.hsp302.shared_english_e_learning_path.repositories;
import com.hsp302.shared_english_e_learning_path.domain.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VideoRepository extends JpaRepository<Video, UUID> {

    List<Video> findByLesson_LessonID(UUID lessonID);
}