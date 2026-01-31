package com.hsp302.shared_english_e_learning_path.mappers;

import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.ForgotPasswordResponse;
import com.hsp302.shared_english_e_learning_path.domain.entities.Password;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PasswordMapper {

    ForgotPasswordResponse toDto(Password password);
}
