package com.hsp302.shared_english_e_learning_path.mappers;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.CreateModuleRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.ModuleResponse;
import com.hsp302.shared_english_e_learning_path.domain.entities.Module;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ModuleMapper {

    Module toModel(CreateModuleRequest request);
    ModuleResponse toDto(Module module);
}
