package com.wesports.backend.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Response DTO for authentication operations
 * Part of the application layer in hexagonal architecture
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponse(
    String status,
    String message,
    String accessToken,  // JWT access token (always in response)
    String refreshToken, // Refresh token (only for mobile clients)
    Long expiresIn,      // Access token expiry in seconds
    UserInfo user        // Basic user information after successful auth
) {
    
    public static AuthResponse success(String message, String accessToken, Long expiresIn, UserInfo user) {
        return new AuthResponse("success", message, accessToken, null, expiresIn, user);
    }
    
    public static AuthResponse success(String message, String accessToken, String refreshToken, Long expiresIn, UserInfo user) {
        return new AuthResponse("success", message, accessToken, refreshToken, expiresIn, user);
    }
    
    public static AuthResponse successWithTokens(String message, String accessToken, String refreshToken, UserInfo user) {
        return new AuthResponse("success", message, accessToken, refreshToken, 3600L, user); // Default 1 hour
    }
    
    public static AuthResponse successWithUser(String message, UserInfo user) {
        return new AuthResponse("success", message, null, null, null, user);
    }
    
    public static AuthResponse error(String message) {
        return new AuthResponse("error", message, null, null, null, null);
    }
    
    /**
     * Basic user information for authentication response
     */
    public record UserInfo(
        String userId,
        String firstName,
        String lastName,
        String email,
        String gender,
        java.time.LocalDate birthday,
        java.time.LocalDateTime createdAt,
        boolean emailVerified
    ) {
        public UserInfo(String userId, String email, String firstName, String lastName, boolean emailVerified) {
            this(userId, firstName, lastName, email, null, null, null, emailVerified);
        }
    }
}
