package com.wesports.backend.domain.exception;

/**
 * Thrown when a token is invalid, expired, or malformed
 * Domain exception for token-related business logic
 */
public class InvalidTokenException extends AuthenticationDomainException {
    
    public InvalidTokenException(String message) {
        super(message, "INVALID_TOKEN");
    }
    
    public InvalidTokenException(String message, Throwable cause) {
        super(message, "INVALID_TOKEN", cause);
    }
    
    @Override
    public boolean isServerError() {
        return false; // Client error - invalid token
    }
}
