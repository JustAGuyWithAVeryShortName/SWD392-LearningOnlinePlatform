package com.hsp302.shared_english_e_learning_path.mappers;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.CreateEnrollmentRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.UpdateEnrollmentRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.EnrollmentResponse;
import com.hsp302.shared_english_e_learning_path.domain.entities.Enrollment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    Enrollment toEntity(CreateEnrollmentRequest request);
    Enrollment toEntity(UpdateEnrollmentRequest request);
    Enrollment toEntity(EnrollmentResponse response);
    EnrollmentResponse toDto(Enrollment enrollment);
}
