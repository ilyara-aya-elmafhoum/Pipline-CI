package com.wesports.backend.application.port.outbound;

import java.time.LocalDateTime;

public interface RefreshTokenService {
    
    /**
     * Generate a refresh token for a user
     * @param userId The user ID
     * @param email The user email
     * @return The generated refresh token
     */
    String generateRefreshToken(String userId, String email);
    
    /**
     * Validate a refresh token
     * @param token The token to validate
     * @return true if valid, false otherwise
     */
    boolean validateRefreshToken(String token);
    
    /**
     * Extract user ID from refresh token
     * @param token The refresh token
     * @return The user ID from the token
     */
    String extractUserIdFromToken(String token);
    
    /**
     * Extract email from refresh token
     * @param token The refresh token
     * @return The email from the token
     */
    String extractEmailFromToken(String token);
    
    /**
     * Check if refresh token is expired
     * @param token The refresh token
     * @return true if expired, false otherwise
     */
    boolean isTokenExpired(String token);
    
    /**
     * Get refresh token expiration time
     * @param token The refresh token
     * @return The expiration time
     */
    LocalDateTime getTokenExpiration(String token);
    
    /**
     * Invalidate a refresh token
     * @param token The token to invalidate
     */
    void invalidateRefreshToken(String token);
    
    /**
     * Invalidate a token (alias for invalidateRefreshToken for compatibility)
     * @param token The token to invalidate
     */
    default void invalidateToken(String token) {
        invalidateRefreshToken(token);
    }
    
    /**
     * Generate new access token from refresh token
     * @param refreshToken The valid refresh token
     * @return New access token
     */
    String generateAccessTokenFromRefreshToken(String refreshToken);
}
