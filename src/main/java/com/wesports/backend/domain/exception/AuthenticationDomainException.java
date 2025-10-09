package com.wesports.backend.domain.exception;

/**
 * Base domain exception for the Ilyara authentication domain
 * All domain-specific exceptions should extend this class
 */
public abstract class AuthenticationDomainException extends RuntimeException {
    
    private final String errorCode;
    
    protected AuthenticationDomainException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    protected AuthenticationDomainException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Indicates if this error should be logged as an error (true) or warning (false)
     */
    public abstract boolean isServerError();
}
