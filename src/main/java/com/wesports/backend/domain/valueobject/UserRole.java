package com.wesports.backend.domain.valueobject;

/**
 * Value object representing user roles in the system
 * Part of the domain layer in hexagonal architecture
 */
public enum UserRole {
    PLAYER,
    CLUB, 
    AGENT,
    COACH,
    REPRESENTANT;
    
    public static UserRole fromString(String role) {
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user role: " + role);
        }
    }
    
    public String getCode() {
        return this.name();
    }
}
