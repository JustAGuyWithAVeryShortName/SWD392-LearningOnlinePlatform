package com.hsp302.shared_english_e_learning_path.repositories;

import com.hsp302.shared_english_e_learning_path.domain.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {
    @Query("""
    SELECT DISTINCT q
    FROM Quiz q
    LEFT JOIN FETCH q.options
    WHERE q.lesson.lessonID = :lessonId
""")
    List<Quiz> findByLessonWithOptions(UUID lessonId);}
