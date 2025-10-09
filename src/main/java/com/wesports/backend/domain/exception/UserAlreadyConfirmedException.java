package com.wesports.backend.domain.exception;

/**
 * Thrown when a user account is already confirmed/activated
 * Domain exception for user state business logic
 */
public class UserAlreadyConfirmedException extends AuthenticationDomainException {
    
    public UserAlreadyConfirmedException() {
        super("User account is already confirmed", "USER_ALREADY_CONFIRMED");
    }
    
    public UserAlreadyConfirmedException(String email) {
        super("User account with email " + email + " is already confirmed", "USER_ALREADY_CONFIRMED");
    }
    
    @Override
    public boolean isServerError() {
        return false; // Client error - business rule violation
    }
}
