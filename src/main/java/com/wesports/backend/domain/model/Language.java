package com.wesports.backend.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Language {
    private UUID id;
    private String name;
    private String code;
    private boolean isActive;

    // Constructor for creating new languages
    public Language(String name, String code) {
        this.id = UUID.randomUUID();
        this.name = validateName(name);
        this.code = validateCode(code);
        this.isActive = true; // Default to active for new languages
    }

    // Constructor for loading from database
    public Language(UUID id, String name, String code, boolean isActive) {
        this.id = Objects.requireNonNull(id, "Language ID cannot be null");
        this.name = validateName(name);
        this.code = validateCode(code);
        this.isActive = isActive;
    }

    // Business logic methods
    public boolean isValid() {
        return name != null && code != null && !name.trim().isEmpty() && !code.trim().isEmpty();
    }

    // Business methods for isActive functionality
    public void activate() {
        this.isActive = true;
    }
    
    public void deactivate() {
        this.isActive = false;
    }
    
    public boolean isActive() {
        return this.isActive;
    }

    // Validation methods
    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Language name cannot be null or empty");
        }
        return name.trim();
    }

    private String validateCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Language code cannot be null or empty");
        }
        
        String normalizedCode = code.trim().toLowerCase();
        
        // Basic validation for ISO language codes (2-3 characters)
        if (normalizedCode.length() < 2 || normalizedCode.length() > 3) {
            throw new IllegalArgumentException("Language code must be 2-3 characters long");
        }
        
        return normalizedCode;
    }

    // Getters
    public UUID getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getCode() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Language language = (Language) o;
        return Objects.equals(id, language.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Language{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
