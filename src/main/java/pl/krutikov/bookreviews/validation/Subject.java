package pl.krutikov.bookreviews.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pl.krutikov.bookreviews.validation.validators.SubjectValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = SubjectValidator.class)
@Target({PARAMETER, FIELD})
@Retention(RUNTIME)
public @interface Subject {

    String message() default "Invalid subject: 20 chars max, only lowercase letters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}