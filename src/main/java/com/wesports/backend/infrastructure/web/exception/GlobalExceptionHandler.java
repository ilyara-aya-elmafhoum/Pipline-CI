package com.wesports.backend.infrastructure.web.exception;

import com.wesports.backend.application.dto.ErrorResponse;
import com.wesports.backend.domain.exception.AuthenticationDomainException;
import com.wesports.backend.domain.exception.InvalidCredentialsException;
import com.wesports.backend.domain.exception.InvalidTokenException;
import com.wesports.backend.domain.exception.UserAlreadyConfirmedException;
import com.wesports.backend.domain.exception.UserAlreadyExistsException;
import com.wesports.backend.domain.exception.UserNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the authentication application
 * Part of the infrastructure layer in hexagonal architecture
 * Handles all exceptions and converts them to standardized ErrorResponse
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handle domain-specific authentication exceptions
     */
    @ExceptionHandler(AuthenticationDomainException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationDomainException(
            AuthenticationDomainException ex, HttpServletRequest request) {
        
        HttpStatus status = determineHttpStatus(ex);
        
        if (ex.isServerError()) {
            logger.error("Domain exception occurred: {}", ex.getMessage(), ex);
        } else {
            logger.warn("Client error: {}", ex.getMessage());
        }
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(status.value())
                .status("error")
                .message(ex.getMessage())
                .code(ex.getErrorCode())
                .timestamp(LocalDateTime.now().toString())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(status).body(errorResponse);
    }
    
    /**
     * Handle Spring Security authentication exceptions
     */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        
        logger.warn("Authentication failed: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED.value())
                .status("error")
                .message("Authentication failed")
                .code("AUTHENTICATION_FAILED")
                .timestamp(LocalDateTime.now().toString())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    /**
     * Handle Spring Security access denied exceptions
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        
        logger.warn("Access denied: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.FORBIDDEN.value())
                .status("error")
                .message("Access denied")
                .code("ACCESS_DENIED")
                .timestamp(LocalDateTime.now().toString())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }
    
    /**
     * Handle validation errors from @Valid annotations
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        logger.warn("Validation failed: {}", ex.getMessage());
        
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .status("error")
                .message("Validation failed")
                .code("VALIDATION_ERROR")
                .timestamp(LocalDateTime.now().toString())
                .path(request.getRequestURI())
                .validationErrors(validationErrors)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle malformed JSON requests
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        
        logger.warn("Malformed request body: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.BAD_REQUEST.value())
                .status("error")
                .message("Malformed request body")
                .code("MALFORMED_REQUEST")
                .timestamp(LocalDateTime.now().toString())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle HTTP method not allowed
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        
        logger.warn("Method not supported: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.METHOD_NOT_ALLOWED.value())
                .status("error")
                .message("HTTP method not supported: " + ex.getMethod())
                .code("METHOD_NOT_ALLOWED")
                .timestamp(LocalDateTime.now().toString())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }
    
    /**
     * Handle 404 Not Found
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest request) {
        
        logger.warn("Endpoint not found: {}", ex.getRequestURL());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.NOT_FOUND.value())
                .status("error")
                .message("Endpoint not found")
                .code("ENDPOINT_NOT_FOUND")
                .timestamp(LocalDateTime.now().toString())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Handle all other unexpected exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .status("error")
                .message("An unexpected error occurred")
                .code("INTERNAL_SERVER_ERROR")
                .timestamp(LocalDateTime.now().toString())
                .path(request.getRequestURI())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Determine appropriate HTTP status for domain exceptions
     */
    private HttpStatus determineHttpStatus(AuthenticationDomainException ex) {
        if (ex instanceof UserNotFoundException) {
            return HttpStatus.NOT_FOUND;
        } else if (ex instanceof InvalidCredentialsException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof InvalidTokenException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof UserAlreadyExistsException) {
            return HttpStatus.CONFLICT;
        } else if (ex instanceof UserAlreadyConfirmedException) {
            return HttpStatus.CONFLICT;
        } else {
            return HttpStatus.BAD_REQUEST;
        }
    }
}
