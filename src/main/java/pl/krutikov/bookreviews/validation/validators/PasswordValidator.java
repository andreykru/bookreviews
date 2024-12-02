package pl.krutikov.bookreviews.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.krutikov.bookreviews.validation.Password;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches("^[\\p{L}\\p{M}\\p{S}\\p{N}\\p{P}]{3,20}$");
    }

}
