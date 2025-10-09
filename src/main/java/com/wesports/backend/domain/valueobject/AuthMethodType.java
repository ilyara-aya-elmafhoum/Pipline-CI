package com.wesports.backend.domain.valueobject;

/**
 * Value object representing authentication method types
 * Part of the domain layer in hexagonal architecture
 */
public enum AuthMethodType {
    WESPORT("wesport", "WeSport Local Authentication"),
    GOOGLE("google", "Google OAuth"),
    APPLE("apple", "Apple Sign In"),
    FACEBOOK("facebook", "Facebook Login"),
    LINKEDIN("linkedin", "LinkedIn OAuth");
    
    private final String code;
    private final String displayName;
    
    AuthMethodType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static AuthMethodType fromCode(String code) {
        for (AuthMethodType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown auth method type: " + code);
    }
    
    public boolean isLocal() {
        return this == WESPORT;
    }
    
    public boolean isOAuth() {
        return !isLocal();
    }
}
