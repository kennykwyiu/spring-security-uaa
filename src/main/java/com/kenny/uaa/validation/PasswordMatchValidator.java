package com.kenny.uaa.validation;

import com.kenny.uaa.annotation.PasswordMatch;
import com.kenny.uaa.domain.dto.UserDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, UserDto> {
    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserDto userDto, ConstraintValidatorContext constraintValidatorContext) {
        return userDto.getPassword().equals(userDto.getMatchingPassword());
    }
}
