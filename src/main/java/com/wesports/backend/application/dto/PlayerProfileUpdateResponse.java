package com.wesports.backend.application.dto;

/**
 * Response DTO for player profile update operations
 */
public record PlayerProfileUpdateResponse(
    String status,    // "success" or "error"
    String message,   // Success/error message
    PlayerProfileResponse updatedProfile  // The updated profile data (null if error)
) {
    
    /**
     * Create success response with updated profile
     */
    public static PlayerProfileUpdateResponse success(String message, PlayerProfileResponse updatedProfile) {
        return new PlayerProfileUpdateResponse("success", message, updatedProfile);
    }
    
    /**
     * Create error response
     */
    public static PlayerProfileUpdateResponse error(String message) {
        return new PlayerProfileUpdateResponse("error", message, null);
    }
}
