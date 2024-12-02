package pl.krutikov.bookreviews.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.krutikov.bookreviews.validation.Subject;

public class SubjectValidator implements ConstraintValidator<Subject, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || value.matches("^[a-z]{1,20}$");
    }

}
