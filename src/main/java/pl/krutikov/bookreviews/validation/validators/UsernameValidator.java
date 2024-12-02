package pl.krutikov.bookreviews.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.krutikov.bookreviews.validation.Username;

public class UsernameValidator implements ConstraintValidator<Username, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches("^[\\p{L}\\p{M}\\p{S}\\p{N}\\p{P}]{1,100}$");
    }

}
