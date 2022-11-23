package com.enfint.application.exception;

public class PreScoringFailedException extends RuntimeException {
    public PreScoringFailedException(String message) {
        super(message);
    }

    public PreScoringFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
