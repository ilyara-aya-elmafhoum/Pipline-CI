package com.wesports.backend.domain.exception;

/**
 * Thrown when a user tries to register with an email that already exists
 * Domain exception for registration business logic
 */
public class UserAlreadyExistsException extends AuthenticationDomainException {
    
    public UserAlreadyExistsException(String email) {
        super("User with email " + email + " already exists", "USER_ALREADY_EXISTS");
    }
    
    @Override
    public boolean isServerError() {
        return false; // Client error - business rule violation
    }
}
