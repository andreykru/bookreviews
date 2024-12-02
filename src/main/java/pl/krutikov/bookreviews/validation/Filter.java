package pl.krutikov.bookreviews.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pl.krutikov.bookreviews.validation.validators.FilterValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = FilterValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Filter {

    String message() default "Invalid filter value";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
