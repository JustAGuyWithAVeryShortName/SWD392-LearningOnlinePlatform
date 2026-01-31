package com.hsp302.shared_english_e_learning_path.mappers;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.CreateProgressRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.ProgressResponse;
import com.hsp302.shared_english_e_learning_path.domain.entities.Progress;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProgressMapper {

    Progress toEntity(CreateProgressRequest request);
    ProgressResponse toDto(Progress progress);
}
