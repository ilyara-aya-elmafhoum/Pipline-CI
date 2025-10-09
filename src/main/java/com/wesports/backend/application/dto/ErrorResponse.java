package com.wesports.backend.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    

    // Status Code
    private int httpStatus;
    
    //  Always "error" for error responses
    private String status;
    
     // Human-readable error message for developers/users
    private String message;
    
    /**
     * Standardized error code for programmatic handling
     */
    private String code;
    
    private String timestamp;
    
    private String path;
    
    /**
     * Field-specific validation errors (for 400 Bad Request)
     * Key = field name, Value = error message
     */
    private Map<String, String> validationErrors;
    
    /**
     * Creates a simple error response with just message and code
     */
    public static ErrorResponse of(String message, String code) {
        ErrorResponse response = new ErrorResponse();
        response.status = "error";
        response.message = message;
        response.code = code;
        return response;
    }
    
    /**
     * Creates an error response with validation errors
     */
    public static ErrorResponse withValidationErrors(String message, String code, Map<String, String> validationErrors) {
        ErrorResponse response = new ErrorResponse();
        response.status = "error";
        response.message = message;
        response.code = code;
        response.validationErrors = validationErrors;
        return response;
    }
}
