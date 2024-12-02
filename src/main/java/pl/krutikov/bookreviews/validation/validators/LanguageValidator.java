package pl.krutikov.bookreviews.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.krutikov.bookreviews.validation.Language;

public class LanguageValidator implements ConstraintValidator<Language, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || value.matches("^[a-z]{2}$");
    }

}
