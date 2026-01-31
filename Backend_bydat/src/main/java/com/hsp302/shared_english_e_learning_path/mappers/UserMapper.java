package com.hsp302.shared_english_e_learning_path.mappers;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.CreateUserRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.UpdateUserRequest;
import com.hsp302.shared_english_e_learning_path.domain.dtos.responses.UserResponse;
import com.hsp302.shared_english_e_learning_path.domain.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(CreateUserRequest request);
    User toEntity(UpdateUserRequest request);
    User toEntity(UserResponse response);
    UserResponse toDto(User user);
}
