package com.hsp302.shared_english_e_learning_path.services;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.QuizRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.SubmitQuizRequest;
import com.hsp302.shared_english_e_learning_path.domain.entities.Lesson;
import com.hsp302.shared_english_e_learning_path.domain.entities.Quiz;
import com.hsp302.shared_english_e_learning_path.domain.entities.QuizOption;
import com.hsp302.shared_english_e_learning_path.domain.entities.QuizSubmission;
import com.hsp302.shared_english_e_learning_path.repositories.LessonRepository;
import com.hsp302.shared_english_e_learning_path.repositories.QuizOptionRepository;
import com.hsp302.shared_english_e_learning_path.repositories.QuizRepository;
import com.hsp302.shared_english_e_learning_path.repositories.QuizSubmissionRepository;
import com.hsp302.shared_english_e_learning_path.security.MyUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepo;
    private final QuizOptionRepository optionRepo;
    private final LessonRepository lessonRepo;
    private final QuizSubmissionRepository quizSubmissionRepository;

    @Transactional
    public Quiz createQuiz(UUID lessonId, QuizRequest request) {

        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        Quiz quiz = Quiz.builder()

                .question(request.getQuestion())
                .type(request.getType())
                .lesson(lesson)
                .build();

        quizRepo.save(quiz);

        List<QuizOption> options = request.getOptions().stream()
                .map(o -> QuizOption.builder()
                        .optionId(UUID.randomUUID())
                        .content(o.getContent())
                        .isCorrect(o.getIsCorrect())
                        .quiz(quiz)
                        .build())
                .toList();
        quiz.setOptions(options);
        optionRepo.saveAll(options);

        return quiz;
    }
    public QuizSubmission submitQuiz(UUID quizId, SubmitQuizRequest request) {

        String username = getCurrentUsername();

        if (quizSubmissionRepository.existsByQuiz_QuizIdAndUsername(quizId, username)) {
            throw new RuntimeException("You already submitted this quiz");
        }

        QuizOption option = optionRepo.findById(request.getOptionId())
                .orElseThrow(() -> new RuntimeException("Option not found"));

        if (!option.getQuiz().getQuizId().equals(quizId)) {
            throw new RuntimeException("Option does not belong to quiz");
        }

        QuizSubmission submission = QuizSubmission.builder()
                .submissionId(UUID.randomUUID())
                .quiz(option.getQuiz())
                .username(username)
                .isCorrect(option.getIsCorrect())
                .build();

        return quizSubmissionRepository.save(submission);
    }
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof MyUserDetails) {
            MyUserDetails myUserDetails = (MyUserDetails) principal;
            return myUserDetails.getUser().getUsername();
        }

        throw new RuntimeException("Invalid authentication principal");
    }
    public List<Quiz> getQuizzesByLesson(UUID lessonId) {
        return quizRepo.findByLessonWithOptions(lessonId);
    }
}