package com.hsp302.shared_english_e_learning_path.mappers;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.CreateAvailabilityRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.UpdateAvailabilityRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.AvailabilityResponse;
import com.hsp302.shared_english_e_learning_path.domain.entities.Availability;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AvailabilityMapper {

    Availability toEntity(CreateAvailabilityRequest request);
    Availability toEntity(UpdateAvailabilityRequest request);
    Availability toEntity(AvailabilityResponse response);
    AvailabilityResponse toDto(Availability availability);
}
