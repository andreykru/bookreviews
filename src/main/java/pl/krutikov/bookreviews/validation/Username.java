package pl.krutikov.bookreviews.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import pl.krutikov.bookreviews.validation.validators.UsernameValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


@Documented
@Constraint(validatedBy = UsernameValidator.class)
@Target({FIELD, TYPE, TYPE_USE})
@Retention(RUNTIME)
public @interface Username {

  String message() default "Invalid username: 100 chars max, only letters, numbers and underscores (_) allowed";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}