package com.wesports.backend.domain.model;

import com.wesports.backend.domain.valueobject.AuthMethodType;
import com.wesports.backend.domain.valueobject.Email;
import com.wesports.backend.domain.valueobject.UserAuthMethodId;
import com.wesports.backend.domain.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * UserAuthMethod domain model representing different authentication methods for a user
 * Supports multiple auth methods per user (WeSport local, Google, Apple, Facebook, etc.)
 * Part of the domain layer in hexagonal architecture
 */
public class UserAuthMethod {
    private UserAuthMethodId id;
    private UserId userId;
    private AuthMethodType authMethodType;
    private String authMethodName; // Display name for this specific auth method
    private Email email; // Email used for this specific auth method
    private String passwordHash; // Only for local auth (WeSport), null for OAuth
    private String externalId; // OAuth provider user ID, null for local auth
    private boolean isPrimary; // Primary authentication method for the user
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastUsedAt;

    // Protected constructor for inheritance/JPA
    protected UserAuthMethod() {
    }

    // Constructor for creating new local auth method (WeSport)
    public UserAuthMethod(UserId userId, Email email, String passwordHash) {
        this.id = UserAuthMethodId.generate();
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.authMethodType = AuthMethodType.WESPORT;
        this.authMethodName = AuthMethodType.WESPORT.getDisplayName();
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "Password hash cannot be null for local auth");
        this.externalId = null; // No external ID for local auth
        this.isPrimary = true; // First auth method is primary by default
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor for creating new OAuth auth method
    public UserAuthMethod(UserId userId, AuthMethodType authMethodType, Email email, String externalId) {
        this.id = UserAuthMethodId.generate();
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.authMethodType = Objects.requireNonNull(authMethodType, "Auth method type cannot be null");
        
        if (authMethodType.isLocal()) {
            throw new IllegalArgumentException("Use local auth constructor for WeSport authentication");
        }
        
        this.authMethodName = authMethodType.getDisplayName();
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.passwordHash = null; // No password for OAuth
        this.externalId = Objects.requireNonNull(externalId, "External ID cannot be null for OAuth");
        this.isPrimary = false; // OAuth methods are secondary by default
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Full constructor for loading from database
    public UserAuthMethod(UserAuthMethodId id, UserId userId, AuthMethodType authMethodType,
                         String authMethodName, Email email, String passwordHash, String externalId,
                         boolean isPrimary, boolean isActive, LocalDateTime createdAt, 
                         LocalDateTime updatedAt, LocalDateTime lastUsedAt) {
        this.id = id;
        this.userId = userId;
        this.authMethodType = authMethodType;
        this.authMethodName = authMethodName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.externalId = externalId;
        this.isPrimary = isPrimary;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.lastUsedAt = lastUsedAt;
    }

    // Business methods
    public void updatePassword(String newPasswordHash) {
        if (!authMethodType.isLocal()) {
            throw new IllegalStateException("Cannot update password for OAuth authentication methods");
        }
        this.passwordHash = Objects.requireNonNull(newPasswordHash, "Password hash cannot be null");
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsPrimary() {
        this.isPrimary = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsSecondary() {
        this.isPrimary = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateLastUsed() {
        this.lastUsedAt = LocalDateTime.now();
    }

    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateAuthMethodName(String newName) {
        this.authMethodName = newName;
        this.updatedAt = LocalDateTime.now();
    }

    // Business rules
    public boolean isLocalAuth() {
        return authMethodType.isLocal();
    }

    public boolean isOAuth() {
        return authMethodType.isOAuth();
    }

    public boolean hasPassword() {
        return passwordHash != null && !passwordHash.trim().isEmpty();
    }

    public boolean canChangePassword() {
        return isLocalAuth() && isActive;
    }

    // Getters
    public UserAuthMethodId getId() { return id; }
    public UserId getUserId() { return userId; }
    public AuthMethodType getAuthMethodType() { return authMethodType; }
    public String getAuthMethodName() { return authMethodName; }
    public Email getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getExternalId() { return externalId; }
    public boolean isPrimary() { return isPrimary; }
    public boolean isActive() { return isActive; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getLastUsedAt() { return lastUsedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAuthMethod that = (UserAuthMethod) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserAuthMethod{" +
                "id=" + id +
                ", userId=" + userId +
                ", authMethodType=" + authMethodType +
                ", email=" + email +
                ", isPrimary=" + isPrimary +
                ", isActive=" + isActive +
                '}';
    }
}
