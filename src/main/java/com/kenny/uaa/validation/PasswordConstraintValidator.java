package com.kenny.uaa.validation;

import com.kenny.uaa.annotation.ValidPassword;
import lombok.RequiredArgsConstructor;
import org.passay.*;
import org.passay.spring.SpringMessageResolver;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

@RequiredArgsConstructor
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private final SpringMessageResolver springMessageResolver;
    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        PasswordValidator validator = new PasswordValidator(
                springMessageResolver,
                Arrays.asList(
                        // Length rule: 8 - 30 characters
                        new LengthRule(8, 30),
                        // At least one uppercase letter
                        new CharacterRule(EnglishCharacterData.UpperCase, 1),
                        // At least one lowercase letter
                        new CharacterRule(EnglishCharacterData.LowerCase, 1),
                        // At least one digit
                        new CharacterRule(EnglishCharacterData.Digit, 1),
                        // At least one special character
                        new CharacterRule(EnglishCharacterData.Special, 1),
                        // Disallow 3 consecutive alphabetical characters
                        // alphabetical is of the form 'abcde', numerical is '34567', qwery is 'asdfg'
                        // The false parameter indicates that wrapped sequences are allowed; e.g. 'xyzabc'
                        new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
                        // Disallow 3 consecutive numerical characters
                        new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false),
                        // Disallow 3 consecutive adjacent characters on the QWERTY keyboard
                        new IllegalSequenceRule(EnglishSequenceData.USQwerty, 5, false),
                        // Disallow whitespace
                        new WhitespaceRule()
                )
        );
        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(String.join(",", validator.getMessages(result)))
                .addConstraintViolation();
        return false;
    }
}
