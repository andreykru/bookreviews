package pl.krutikov.bookreviews.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pl.krutikov.bookreviews.validation.validators.LanguageValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = LanguageValidator.class)
@Target({PARAMETER, FIELD})
@Retention(RUNTIME)
public @interface Language {

    String message() default "Invalid language code";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
