package pl.krutikov.bookreviews.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pl.krutikov.bookreviews.validation.Filter;

import java.util.Set;

public class FilterValidator implements ConstraintValidator<Filter, String> {

    private static final Set<String> VALID_FILTERS = Set.of("ebooks", "free-ebooks", "paid-ebooks", "full", "partial");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || VALID_FILTERS.contains(value);
    }

}
