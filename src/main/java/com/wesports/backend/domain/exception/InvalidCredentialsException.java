package com.wesports.backend.domain.exception;

/**
 * Thrown when authentication credentials are invalid
 * Domain exception for authentication business logic
 */
public class InvalidCredentialsException extends AuthenticationDomainException {
    
    public InvalidCredentialsException() {
        super("Invalid email or password", "INVALID_CREDENTIALS");
    }
    
    public InvalidCredentialsException(String message) {
        super(message, "INVALID_CREDENTIALS");
    }
    
    @Override
    public boolean isServerError() {
        return false; // Client error - invalid credentials
    }
}
