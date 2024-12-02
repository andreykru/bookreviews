package pl.krutikov.bookreviews.exception;

import org.springframework.http.HttpStatus;

import static pl.krutikov.bookreviews.exceptionhandler.ExceptionCode.BAD_REQUEST_ERROR;


public class BadRequestException extends ApplicationException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, BAD_REQUEST_ERROR, message);
    }

}
