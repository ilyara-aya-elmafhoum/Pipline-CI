package com.wesports.backend.domain.model;

import com.wesports.backend.domain.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class OTP {
    private UUID id;
    private UserId userId;
    private String otpCode;
    private LocalDateTime createdAt;
    private Integer attempts;
    private String type;
    private String languageCode;

    // Constants
    private static final int OTP_LENGTH = 6;
    private static final int MAX_ATTEMPTS = 3;
    private static final int OTP_VALIDITY_MINUTES = 10;

    // Constructor for creating new OTP
    public OTP(UserId userId, String type) {
        this.id = UUID.randomUUID();
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.type = validateType(type);
        this.otpCode = generateOTPCode();
        this.createdAt = LocalDateTime.now();
        this.attempts = 0;
        this.languageCode = null; // Default to null, will be set if needed
    }
    
    // Constructor for creating new OTP with language code (for registration)
    public OTP(UserId userId, String type, String languageCode) {
        this.id = UUID.randomUUID();
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.type = validateType(type);
        this.otpCode = generateOTPCode();
        this.createdAt = LocalDateTime.now();
        this.attempts = 0;
        this.languageCode = languageCode; // Store user's language choice
    }

    // Constructor for loading from database
    public OTP(UUID id, UserId userId, String otpCode, LocalDateTime createdAt, Integer attempts, String type) {
        this.id = Objects.requireNonNull(id, "OTP ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.otpCode = Objects.requireNonNull(otpCode, "OTP code cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.attempts = attempts != null ? attempts : 0;
        this.type = validateType(type);
        this.languageCode = null; // Legacy OTPs won't have language code
    }
    
    // Constructor for loading from database with language code
    public OTP(UUID id, UserId userId, String otpCode, LocalDateTime createdAt, Integer attempts, String type, String languageCode) {
        this.id = Objects.requireNonNull(id, "OTP ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.otpCode = Objects.requireNonNull(otpCode, "OTP code cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.attempts = attempts != null ? attempts : 0;
        this.type = validateType(type);
        this.languageCode = languageCode; // Store user's language choice
    }

    // Business logic methods
    public boolean isValid() {
        return !isExpired() && !isMaxAttemptsReached();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(createdAt.plusMinutes(OTP_VALIDITY_MINUTES));
    }

    public boolean isMaxAttemptsReached() {
        return attempts >= MAX_ATTEMPTS;
    }

    public boolean verify(String inputCode) {
        if (!isValid()) {
            return false;
        }

        incrementAttempts();
        return Objects.equals(this.otpCode, inputCode);
    }

    public void incrementAttempts() {
        this.attempts++;
    }

    public long getMinutesUntilExpiry() {
        LocalDateTime expiryTime = createdAt.plusMinutes(OTP_VALIDITY_MINUTES);
        LocalDateTime now = LocalDateTime.now();
        
        if (now.isAfter(expiryTime)) {
            return 0;
        }
        
        return java.time.Duration.between(now, expiryTime).toMinutes();
    }

    public int getRemainingAttempts() {
        return Math.max(0, MAX_ATTEMPTS - attempts);
    }

    // Private methods
    private String generateOTPCode() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        
        return otp.toString();
    }

    private String validateType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("OTP type cannot be null or empty");
        }
        return type.trim().toUpperCase();
    }

    // Getters
    public UUID getId() { return id; }
    public UserId getUserId() { return userId; }
    public String getOtpCode() { return otpCode; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Integer getAttempts() { return attempts; }
    public String getType() { return type; }
    public String getLanguageCode() { return languageCode; } // TEMPORARY: Get user's language choice

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OTP otp = (OTP) o;
        return Objects.equals(id, otp.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OTP{" +
                "id=" + id +
                ", userId=" + userId +
                ", type='" + type + '\'' +
                ", attempts=" + attempts +
                ", createdAt=" + createdAt +
                ", isValid=" + isValid() +
                '}';
    }
}
