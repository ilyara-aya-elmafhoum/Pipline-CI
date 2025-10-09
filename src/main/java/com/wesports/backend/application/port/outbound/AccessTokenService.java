package com.wesports.backend.application.port.outbound;

import io.jsonwebtoken.Claims;
import java.time.LocalDateTime;

public interface AccessTokenService {
    
    /**
     * Generate an access token for a user
     * @param userId The user ID
     * @param email The user email
     * @return The generated access token
     */
    String generateAccessToken(String userId, String email);
    
    /**
     * Validate an access token and return claims if valid
     * @param token The token to validate
     * @return The claims if valid, null if invalid/expired
     */
    Claims validateAccessToken(String token);
    
    /**
     * Extract user ID from token
     * @param token The access token
     * @return The user ID from the token, null if invalid
     */
    String extractUserId(String token);
    
    /**
     * Extract email from token
     * @param token The access token
     * @return The email from the token, null if invalid
     */
    String extractEmail(String token);
    
    /**
     * Check if token is expired
     * @param token The access token
     * @return true if expired, false otherwise
     */
    boolean isTokenExpired(String token);
    
    /**
     * Get token expiration time
     * @param token The access token
     * @return The expiration time
     */
    LocalDateTime getTokenExpiration(String token);
}
