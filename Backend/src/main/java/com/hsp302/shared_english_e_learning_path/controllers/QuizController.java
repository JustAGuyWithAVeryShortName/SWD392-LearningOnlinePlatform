package com.hsp302.shared_english_e_learning_path.controllers;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.QuizRequest;
import com.hsp302.shared_english_e_learning_path.domain.entities.Quiz;
import com.hsp302.shared_english_e_learning_path.services.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping("/lesson/{lessonId}")
    public Quiz createQuiz(
            @PathVariable UUID lessonId,
            @RequestBody QuizRequest request) {

        return quizService.createQuiz(lessonId, request);
    }
    @GetMapping("/lessons/{lessonId}")
    public List<Quiz> getQuizzesByLesson(@PathVariable UUID lessonId) {
        return quizService.getQuizzesByLesson(lessonId);
    }
}