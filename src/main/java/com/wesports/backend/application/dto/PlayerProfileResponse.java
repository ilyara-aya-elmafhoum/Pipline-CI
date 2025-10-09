package com.wesports.backend.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Player profile response DTO for /me endpoint
 * Contains all player data for frontend consumption
 */
public record PlayerProfileResponse(
    // User base fields
    UUID id,
    String firstName,
    String lastName,
    String email,
    String gender,
    LocalDate birthday,
    UUID languageId,
    List<String> authProviders,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String userRole,
    String nationality,
    String lieuDeResidence,
    List<String> languages,
    boolean emailVerified,
    
    // Player-specific fields
    String profilePhotoUrl,
    Float height,
    Float weight,
    UUID postId,
    String position,
    String category,
    
    // Profile completion status
    boolean isProfileComplete,
    boolean hasPosition,
    boolean hasCategory,
    boolean isPhysicalProfileComplete
) {
    
    /**
     * Create response for successful profile retrieval
     */
    public static PlayerProfileResponse success(
            UUID id, String firstName, String lastName, String email, String gender,
            LocalDate birthday, UUID languageId, List<String> authProviders, 
            LocalDateTime createdAt, LocalDateTime updatedAt, String userRole,
            String nationality, String lieuDeResidence, List<String> languages, 
            boolean emailVerified, String profilePhotoUrl, Float height, Float weight,
            UUID postId, String position, String category, boolean isProfileComplete,
            boolean hasPosition, boolean hasCategory, boolean isPhysicalProfileComplete) {
        
        return new PlayerProfileResponse(
            id, firstName, lastName, email, gender, birthday, languageId,
            authProviders, createdAt, updatedAt, userRole, nationality,
            lieuDeResidence, languages, emailVerified, profilePhotoUrl,
            height, weight, postId, position, category, isProfileComplete,
            hasPosition, hasCategory, isPhysicalProfileComplete
        );
    }
}
