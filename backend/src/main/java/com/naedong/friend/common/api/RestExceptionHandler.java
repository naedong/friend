package com.naedong.friend.common.api;

import com.naedong.friend.common.DomainException;
import com.naedong.friend.common.DomainNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(DomainNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(DomainNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of("NOT_FOUND", exception.getMessage()));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> domain(DomainException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of("DOMAIN_RULE_VIOLATION", exception.getMessage()));
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ErrorResponse> badRequest(Exception exception) {
        return ResponseEntity.badRequest()
                .body(ErrorResponse.of("BAD_REQUEST", exception.getMessage()));
    }
}
