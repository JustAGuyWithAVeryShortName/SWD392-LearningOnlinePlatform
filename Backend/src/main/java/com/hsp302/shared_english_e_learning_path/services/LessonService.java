package com.hsp302.shared_english_e_learning_path.services;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.CreateLessonRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.DeleteLessonsRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.UpdateLessonRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.LessonResponse;
import com.hsp302.shared_english_e_learning_path.domain.entities.Lesson;
import com.hsp302.shared_english_e_learning_path.domain.entities.Module;
import com.hsp302.shared_english_e_learning_path.domain.entities.Quiz;
import com.hsp302.shared_english_e_learning_path.domain.entities.QuizOption;
import com.hsp302.shared_english_e_learning_path.domain.enums.CourseStatus;
import com.hsp302.shared_english_e_learning_path.mappers.LessonMapper;
import com.hsp302.shared_english_e_learning_path.repositories.LessonRepository;
import com.hsp302.shared_english_e_learning_path.repositories.QuizRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final LessonMapper lessonMapper;
    private final ModuleService moduleService;
    private final BlogService blogService;
    private final QuizRepository quizRepository;

    public LessonResponse createLesson(CreateLessonRequest request) {
        Lesson lesson = lessonMapper.toEntity(request);
        lesson.setLessonID(UUID.randomUUID());
        lesson.setStatus(CourseStatus.AVAILABLE);
        lesson.setDuration(calculateDuration(request.getContent()));
        UUID moduleID = request.getModuleID();
        Module module = moduleService.getModelEntity(moduleID);
        lesson.setModule(module);
        lessonRepository.save(lesson);
        if (request.getQuizzes() != null && !request.getQuizzes().isEmpty()) {
            request.getQuizzes().forEach(q -> {

                Quiz quiz = Quiz.builder()
                        .question(q.getQuestion())
                        .lesson(lesson)
                        .build();

                List<QuizOption> options = q.getOptions().stream()
                        .map(o -> QuizOption.builder()
                                .content(o.getContent())
                                .isCorrect(o.getIsCorrect())
                                .quiz(quiz)
                                .build())
                        .toList();

                quiz.setOptions(options);

                quizRepository.save(quiz);
            });
        }
        return lessonMapper.toDto(lesson);
    }

    public List<LessonResponse> getLessons() {
        return lessonRepository.findAll().stream().map(lessonMapper::toDto).toList();
    }

    public List<Lesson> getLessonsByModuleID(UUID moduleID, CourseStatus status) {
        return lessonRepository.findByModuleModuleIDAndStatus(moduleID, status);
    }

    public List<LessonResponse> getLessonsForModule(UUID moduleID) {
        List<Lesson> lessons = getLessonsByModuleID(moduleID, CourseStatus.AVAILABLE);
        return lessons.stream()
                .map(lesson -> lessonMapper.toDto(lesson))
                .toList();
    }

    public Lesson getLessonEntity(UUID lessonID) {
        return lessonRepository.findById(lessonID)
                .orElseThrow(() -> new EntityNotFoundException("Lesson does not exist with ID: " + lessonID));
    }

    public LessonResponse getLesson(UUID lessonID) {
        Lesson lesson = getLessonEntity(lessonID);
        return lessonMapper.toDto(lesson);
    }

    @Transactional
    public LessonResponse updateLesson(UUID lessonID, UpdateLessonRequest request) {
        Lesson lesson = getLessonEntity(lessonID);

        lesson.setLessonName(request.getLessonName());
        lesson.setDuration(calculateDuration(request.getContent()));
        lesson.setObjective(request.getObjective());
        lesson.setContent(request.getContent());

        // ❌ Xoá quiz cũ
        lesson.getQuizzes().clear();

        // ✅ Add quiz mới
        if (request.getQuizzes() != null) {
            List<Quiz> quizzes = request.getQuizzes().stream().map(q -> {
                Quiz quiz = Quiz.builder()
                        .question(q.getQuestion())
                        .lesson(lesson)
                        .build();

                List<QuizOption> options = q.getOptions().stream()
                        .map(o -> QuizOption.builder()
                                .content(o.getContent())
                                .isCorrect(o.getIsCorrect())
                                .quiz(quiz)
                                .build())
                        .toList();

                quiz.setOptions(options);
                return quiz;
            }).toList();

            lesson.getQuizzes().addAll(quizzes);
        }

        lessonRepository.save(lesson);
        return lessonMapper.toDto(lesson);
    }

    public List<LessonResponse> updateLessonsStatus(UUID moduleID, DeleteLessonsRequest request) {
        List<UUID> existingLessonIDs = getLessonsByModuleID(moduleID, CourseStatus.AVAILABLE).stream()
                .map(Lesson::getLessonID).toList();
        List<UUID> requestedLessonIDs = request.getLessonIds();
        List<Lesson> lessons = new ArrayList<>();
        for (UUID id : requestedLessonIDs) {
            if (existingLessonIDs.contains(id)) {
                Lesson lesson = getLessonEntity(id);
                lesson.setStatus(request.getStatus());
                lessonRepository.save(lesson);
                lessons.add(lesson);
            }
        }
        return lessons.stream().map(lesson -> lessonMapper.toDto(lesson)).toList();
    }

    public int countLessonsByCourseId(UUID courseID) {
        return lessonRepository.countLessonsByCourseId(courseID);
    }

    private int calculateDuration(String content){
        return blogService.calculateReadingTime(content);
    }
}
