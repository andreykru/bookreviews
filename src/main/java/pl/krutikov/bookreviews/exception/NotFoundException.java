package pl.krutikov.bookreviews.exception;

import org.springframework.http.HttpStatus;

import static pl.krutikov.bookreviews.exceptionhandler.ExceptionCode.NOT_FOUND_ERROR;

public class NotFoundException extends ApplicationException {

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, NOT_FOUND_ERROR, message);
    }

}
