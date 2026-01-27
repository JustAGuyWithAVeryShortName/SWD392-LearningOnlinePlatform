package com.hsp302.shared_english_e_learning_path.mappers;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.CreateCourseRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.UpdateCourseRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.CourseResponse;
import com.hsp302.shared_english_e_learning_path.domain.entities.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    Course toEntity(CreateCourseRequest request);
    Course toEntity(UpdateCourseRequest request);
    Course toEntity(CourseResponse response);
    CourseResponse toDto(Course course);
}
