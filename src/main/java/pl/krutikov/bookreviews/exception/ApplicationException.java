package pl.krutikov.bookreviews.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class ApplicationException extends RuntimeException {

    private final String code;
    private final HttpStatus httpStatus;
    private final Map<String, String> details;

    public ApplicationException(HttpStatus httpStatus, String code, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
        this.details = null;
    }

    public ApplicationException(HttpStatus httpStatus, String code, String message, Map<String, String> details) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
        this.details = details;
    }

}
