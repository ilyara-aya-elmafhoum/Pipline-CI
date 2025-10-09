package com.wesports.backend.domain.valueobject;

/**
 * Value object representing authentication provider types
 * Part of the domain layer in hexagonal architecture
 */
public enum AuthProviderType {
    LOCAL("WeSports", "WeSports native authentication"),
    GOOGLE("Google", "Google OAuth2 authentication"),
    FACEBOOK("Facebook", "Facebook OAuth2 authentication"),
    LINKEDIN("LinkedIn", "LinkedIn OAuth2 authentication"),
    APPLE("Apple", "Apple ID authentication");

    private final String displayName;
    private final String description;

    AuthProviderType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isOAuth() {
        return this != LOCAL;
    }

    public boolean isLocal() {
        return this == LOCAL;
    }

    public static AuthProviderType fromString(String providerType) {
        if (providerType == null || providerType.trim().isEmpty()) {
            throw new IllegalArgumentException("Provider type cannot be null or empty");
        }
        
        try {
            return AuthProviderType.valueOf(providerType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid auth provider type: " + providerType);
        }
    }

    public static AuthProviderType fromDisplayName(String displayName) {
        if (displayName == null || displayName.trim().isEmpty()) {
            throw new IllegalArgumentException("Display name cannot be null or empty");
        }
        
        for (AuthProviderType type : values()) {
            if (type.displayName.equalsIgnoreCase(displayName.trim())) {
                return type;
            }
        }
        
        throw new IllegalArgumentException("Invalid auth provider display name: " + displayName);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
