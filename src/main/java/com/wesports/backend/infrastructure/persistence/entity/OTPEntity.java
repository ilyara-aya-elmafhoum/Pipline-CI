package com.wesports.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "otp")
public class OTPEntity {
    
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;
    
    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;
    
    @Column(name = "otp_code", nullable = false)
    private String otpCode;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "attempts", nullable = false)
    private Integer attempts;
    
    @Column(name = "type", nullable = false)
    private String type;
    
    @Column(name = "language_code")
    private String languageCode;

    public OTPEntity() {}

    // Constructor
    public OTPEntity(UUID id, UUID userId, String otpCode, LocalDateTime createdAt, 
                    Integer attempts, String type) {
        this.id = id;
        this.userId = userId;
        this.otpCode = otpCode;
        this.createdAt = createdAt;
        this.attempts = attempts;
        this.type = type;
        this.languageCode = null;
    }
    
    // Constructor with language code
    public OTPEntity(UUID id, UUID userId, String otpCode, LocalDateTime createdAt, 
                    Integer attempts, String type, String languageCode) {
        this.id = id;
        this.userId = userId;
        this.otpCode = otpCode;
        this.createdAt = createdAt;
        this.attempts = attempts;
        this.type = type;
        this.languageCode = languageCode;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getOtpCode() { return otpCode; }
    public void setOtpCode(String otpCode) { this.otpCode = otpCode; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getAttempts() { return attempts; }
    public void setAttempts(Integer attempts) { this.attempts = attempts; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getLanguageCode() { return languageCode; }
    public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }
}
