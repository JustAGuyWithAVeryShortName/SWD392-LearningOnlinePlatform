package com.hsp302.shared_english_e_learning_path.mappers;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.CreateQualificationRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.UpdateQualificationRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.QualificationResponse;
import com.hsp302.shared_english_e_learning_path.domain.entities.Qualification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QualificationMapper {

    Qualification toEntity(CreateQualificationRequest request);
    Qualification toEntity(UpdateQualificationRequest request);
    Qualification toEntity(QualificationResponse response);
    QualificationResponse toDto(Qualification qualification);
}
