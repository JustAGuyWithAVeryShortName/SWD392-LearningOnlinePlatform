package com.hsp302.shared_english_e_learning_path.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = {PasswordMatchesValidator.class})
public @interface PasswordMatches {
    String message() default "Password and Confirm Password do not match}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
