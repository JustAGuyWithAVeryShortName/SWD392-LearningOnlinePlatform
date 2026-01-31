package com.hsp302.shared_english_e_learning_path.mappers;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.CreateLessonRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.UpdateLessonRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.LessonResponse;
import com.hsp302.shared_english_e_learning_path.domain.entities.Lesson;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LessonMapper {

    Lesson toEntity(CreateLessonRequest request);
    Lesson toEntity(UpdateLessonRequest request);
    LessonResponse toDto(Lesson lesson);
}
