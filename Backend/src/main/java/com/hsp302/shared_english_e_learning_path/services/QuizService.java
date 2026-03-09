package com.hsp302.shared_english_e_learning_path.services;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.QuizRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.SubmitQuizRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.QuizResultResponse;
import com.hsp302.shared_english_e_learning_path.domain.entities.Lesson;
import com.hsp302.shared_english_e_learning_path.domain.entities.Quiz;
import com.hsp302.shared_english_e_learning_path.domain.entities.QuizOption;
import com.hsp302.shared_english_e_learning_path.domain.entities.QuizSubmission;
import com.hsp302.shared_english_e_learning_path.repositories.LessonRepository;
import com.hsp302.shared_english_e_learning_path.repositories.QuizOptionRepository;
import com.hsp302.shared_english_e_learning_path.repositories.QuizRepository;
import com.hsp302.shared_english_e_learning_path.repositories.QuizSubmissionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
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
    @Transactional
    public QuizResultResponse submitAssignment(SubmitQuizRequest request) {

        String username = getCurrentUsername();

        List<Quiz> quizzes =
                quizRepo.findByLessonWithOptions(request.getLessonId());

        if (quizzes.isEmpty()) {
            throw new RuntimeException("Lesson has no quiz");
        }

        // ✅ chặn nộp lại assignment
        boolean submitted = quizzes.stream()
                .anyMatch(q ->
                        quizSubmissionRepository
                                .existsByUsernameAndQuiz_QuizId(username, q.getQuizId())
                );

        if (submitted) {
            throw new RuntimeException("Assignment already submitted");
        }

        int total = quizzes.size();
        int correct = 0;

        for (Quiz quiz : quizzes) {

            UUID selectedOptionId =
                    request.getAnswers().get(quiz.getQuizId());

            if (selectedOptionId == null) continue;

            QuizOption option = quiz.getOptions().stream()
                    .filter(o -> o.getOptionId().equals(selectedOptionId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Option not found"));

            boolean isCorrect = Boolean.TRUE.equals(option.getIsCorrect());

            quizSubmissionRepository.save(
                    QuizSubmission.builder()
                            .submissionId(UUID.randomUUID())
                            .username(username)
                            .quiz(quiz)
                            .isCorrect(isCorrect)
                            .build()
            );

            if (isCorrect) correct++;
        }

        int score = Math.round(correct * 100f / total);

        return QuizResultResponse.builder()
                .total(total)
                .correct(correct)
                .score(score)
                .passed(score >= 70)
                .build();
    }


    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated");
        }

        Object principal = auth.getPrincipal();

        // ✅ JWT principal
        if (principal instanceof Jwt jwt) {
            return jwt.getSubject(); // chính là username
        }

        throw new RuntimeException(
                "Invalid authentication principal: " + principal.getClass().getName()
        );
    }
    public List<Quiz> getQuizzesByLesson(UUID lessonId) {
        return quizRepo.findByLessonWithOptions(lessonId);
    }
}