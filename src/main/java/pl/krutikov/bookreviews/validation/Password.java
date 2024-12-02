package pl.krutikov.bookreviews.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pl.krutikov.bookreviews.validation.validators.PasswordValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({FIELD, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface Password {

    String message() default "Invalid password: 3 chars min, 20 chars max, only letters, digits and common special characters are allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
