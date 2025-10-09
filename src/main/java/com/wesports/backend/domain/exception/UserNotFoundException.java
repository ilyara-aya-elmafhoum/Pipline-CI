package com.wesports.backend.domain.exception;

/**
 * Thrown when a user is not found during authentication operations
 * Domain exception indicating business rule violation
 */
public class UserNotFoundException extends AuthenticationDomainException {
    
    public UserNotFoundException(String email) {
        super("User not found with email: " + email, "USER_NOT_FOUND");
    }
    
    public UserNotFoundException(Long userId) {
        super("User not found with ID: " + userId, "USER_NOT_FOUND");
    }
    
    @Override
    public boolean isServerError() {
        return false; // Client error - user doesn't exist
    }
}
