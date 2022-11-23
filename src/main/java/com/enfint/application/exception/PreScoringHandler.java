package com.enfint.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class PreScoringHandler {
    @ExceptionHandler(value = {PreScoringFailedException.class})
    public ResponseEntity<Object> handleRefusalException(PreScoringFailedException e){
        PreScoringFailed preScoringFailed = new PreScoringFailed(
                e.getMessage(),
                HttpStatus.UNAUTHORIZED,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(preScoringFailed,HttpStatus.UNAUTHORIZED);
    }
}
