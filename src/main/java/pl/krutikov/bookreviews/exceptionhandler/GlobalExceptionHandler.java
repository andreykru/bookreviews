package pl.krutikov.bookreviews.exceptionhandler;

import feign.FeignException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.krutikov.bookreviews.exception.ApplicationException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String MESSAGE_KEY = "message";

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException exception,
                                                                WebRequest request) {

        log.error("Handling {}. Message: {}", exception.getClass(), exception.getMessage());
        return handleException(exception, HttpStatus.UNAUTHORIZED, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException exception,
                                                              WebRequest request) {

        log.error("Handling {}. Message: {}", exception.getClass(), exception.getMessage());
        return handleException(exception, HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException exception,
                                                            WebRequest request) {

        log.error("Handling {}. Message: {}", exception.getClass(), exception.getMessage());
        return handleException(exception, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<Object> handleFeignException(FeignException exception,
                                                       WebRequest request) {

        log.error("Handling {}. Message: {}", exception.getClass(), exception.getMessage());
        return handleException(exception, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(CompletionException.class)
    public ResponseEntity<Object> handleCompletionException(CompletionException exception,
                                                            WebRequest request) {

        log.error("Handling {}. Message: {}", exception.getClass(), exception.getMessage());

        if (exception.getCause() instanceof FeignException.NotFound feignException) {
            return handleFeignException(feignException, request);
        }
        if (exception.getCause() instanceof ApplicationException applicationException) {
            return handleApplicationException(applicationException, request);
        }

        return handleGeneralException(exception, request);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Object> handleApplicationException(ApplicationException exception,
                                                             WebRequest request) {

        log.error("Handling {}. Message: {}", exception.getClass(), exception.getMessage());
        return handleException(exception, exception.getHttpStatus(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception exception,
                                                         WebRequest request) {

        log.error("Handling {}. Message: {}", exception.getClass(), exception.getMessage());
        return handleException(exception, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {

        log.error("Handling {}. Message: {}", exception.getClass(), exception.getMessage());
        ExceptionResponse response = getExceptionResponse(exception, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(response, headers, HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
    }

    private ResponseEntity<Object> handleException(Exception exception,
                                                   HttpStatus status,
                                                   WebRequest request) {
        ExceptionResponse response = getExceptionResponse(exception, status);

        return handleExceptionInternal(exception, response, new HttpHeaders(), status, request);
    }

    private ExceptionResponse getExceptionResponse(Exception exception, HttpStatus status) {
        return new ExceptionResponse()
                .setTimestamp(LocalDateTime.now())
                .setStatus(status.value())
                .setCode(getExceptionCode(exception))
                .setDetails(getExceptionDetails(exception));
    }

    private Map<String, String> getExceptionDetails(Exception exception) {
        if (exception instanceof ConstraintViolationException constraintViolationException) {
            return constraintViolationException.getConstraintViolations().stream()
                    .filter(violation -> Objects.nonNull(violation.getMessage()))
                    .collect(Collectors.toMap(
                            violation -> violation.getPropertyPath().toString(),
                            ConstraintViolation::getMessage,
                            (key, duplicateKey) -> key
                    ));
        }

        if (exception instanceof BindException bindException) {
            return bindException.getBindingResult().getFieldErrors().stream()
                    .filter(fieldError -> Objects.nonNull(fieldError.getDefaultMessage()))
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            FieldError::getDefaultMessage,
                            (key, duplicateKey) -> key
                    ));
        }

        return Objects.nonNull(exception.getMessage())
                ? Map.of(MESSAGE_KEY, exception.getMessage())
                : null;
    }

    private String getExceptionCode(Exception exception) {
        if (exception instanceof ConstraintViolationException) {
            return ExceptionCode.VALIDATION_ERROR;
        }
        if (exception instanceof BindException) {
            return ExceptionCode.VALIDATION_ERROR;
        }
        if (exception instanceof FeignException.NotFound) {
            return ExceptionCode.NOT_FOUND_ERROR;
        }
        if (exception instanceof AuthenticationException) {
            return HttpStatus.UNAUTHORIZED.name();
        }
        if (exception instanceof AccessDeniedException) {
            return HttpStatus.FORBIDDEN.name();
        }
        if (exception instanceof ApplicationException applicationException) {
            return applicationException.getCode();
        }

        return ExceptionCode.APPLICATION_ERROR;
    }

}
