package com.wesports.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA Entity for UserAuthMethod
 * Maps to user_auth_methods table
 * Part of the infrastructure layer in hexagonal architecture
 */
@Entity
@Table(name = "user_auth_methods")
public class UserAuthMethodEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_method_type", nullable = false)
    private AuthMethodTypeEnum authMethodType;
    
    @Column(name = "auth_method_name", nullable = false)
    private String authMethodName;
    
    @Column(name = "email", nullable = false)
    private String email;
    
    @Column(name = "password_hash") // Only for local auth (WeSport)
    private String passwordHash;
    
    @Column(name = "external_id") // OAuth provider user ID
    private String externalId;
    
    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary;
    
    @Column(name = "is_active", nullable = false)
    private boolean isActive;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    // Auth method type enum for JPA entity
    public enum AuthMethodTypeEnum {
        WESPORT, GOOGLE, APPLE, FACEBOOK, LINKEDIN
    }

    // Default constructor for JPA
    public UserAuthMethodEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    // Full constructor
    public UserAuthMethodEntity(UUID id, UUID userId, AuthMethodTypeEnum authMethodType, String authMethodName,
                               String email, String passwordHash, String externalId, boolean isPrimary,
                               boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt,
                               LocalDateTime lastUsedAt) {
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

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public AuthMethodTypeEnum getAuthMethodType() { return authMethodType; }
    public void setAuthMethodType(AuthMethodTypeEnum authMethodType) { this.authMethodType = authMethodType; }

    public String getAuthMethodName() { return authMethodName; }
    public void setAuthMethodName(String authMethodName) { this.authMethodName = authMethodName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }

    public boolean isPrimary() { return isPrimary; }
    public void setPrimary(boolean primary) { isPrimary = primary; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(LocalDateTime lastUsedAt) { this.lastUsedAt = lastUsedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAuthMethodEntity that = (UserAuthMethodEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserAuthMethodEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", authMethodType=" + authMethodType +
                ", email='" + email + '\'' +
                ", isPrimary=" + isPrimary +
                ", isActive=" + isActive +
                '}';
    }
}
