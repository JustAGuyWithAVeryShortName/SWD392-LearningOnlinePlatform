package com.hsp302.shared_english_e_learning_path.validators;

import com.hsp302.shared_english_e_learning_path.domain.dtos.requests.CreateUserRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, CreateUserRequest> {

    @Override
    public boolean isValid(CreateUserRequest request, ConstraintValidatorContext context) {
        if (request.getPassword() == null || request.getConfirm() == null) return true;
        return request.getPassword().equals(request.getConfirm());
    }
}
