package com.hsp302.shared_english_e_learning_path.mappers;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.CreateLessonRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.UpdateLessonRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.LessonResponse;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.QuizOptionResponse;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.QuizResponse;
import com.hsp302.shared_english_e_learning_path.domain.entities.Lesson;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonMapper {

    Lesson toEntity(CreateLessonRequest request);
    Lesson toEntity(UpdateLessonRequest request);
   // LessonResponse toDto(Lesson lesson);
   default LessonResponse toDto(Lesson lesson) {
       return LessonResponse.builder()
               .lessonID(lesson.getLessonID())
               .lessonName(lesson.getLessonName())
               .objective(lesson.getObjective())
               .content(lesson.getContent())
               .resource(lesson.getResource())
               .duration(lesson.getDuration())
               .quizzes(
                       lesson.getQuizzes() == null ? List.of() :
                               lesson.getQuizzes().stream().map(q ->
                                       QuizResponse.builder()
                                               .quizId(q.getQuizId())
                                               .question(q.getQuestion())
                                               .options(
                                                       q.getOptions().stream().map(o ->
                                                               QuizOptionResponse.builder()
                                                                       .optionId(o.getOptionId())
                                                                       .content(o.getContent())
                                                                       .build()
                                                       ).toList()
                                               )
                                               .build()
                               ).toList()
               )
               .build();
   }

}
