package com.hsp302.shared_english_e_learning_path.repositories;

import com.hsp302.shared_english_e_learning_path.domain.entities.QuizSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuizSubmissionRepository
        extends JpaRepository<QuizSubmission, UUID> {

    boolean existsByQuiz_QuizIdAndUsername(UUID quizId, String username);
}
