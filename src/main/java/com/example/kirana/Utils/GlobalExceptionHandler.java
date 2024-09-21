package com.example.kirana.Utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandler {
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<String> handleRateLimitExceeded(RateLimitExceededException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
    }
}
