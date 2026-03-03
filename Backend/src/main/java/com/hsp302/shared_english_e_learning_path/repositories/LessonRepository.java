package com.hsp302.shared_english_e_learning_path.repositories;

import com.hsp302.shared_english_e_learning_path.domain.entities.Lesson;
import com.hsp302.shared_english_e_learning_path.domain.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface LessonRepository extends JpaRepository<Lesson, UUID> {

    @Query("SELECT COUNT(l) FROM Lesson l JOIN l.module m JOIN m.course c WHERE c.courseID = :courseID")
    int countLessonsByCourseId(UUID courseID);


    List<Lesson> findByModuleModuleIDAndStatus(UUID moduleID, CourseStatus status);


}
