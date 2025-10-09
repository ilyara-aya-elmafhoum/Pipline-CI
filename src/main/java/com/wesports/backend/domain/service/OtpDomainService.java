package com.wesports.backend.domain.service;

import com.wesports.backend.domain.model.OTP;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Domain service for OTP business logic
 * Contains pure business rules without infrastructure concerns
 */
@Service
@Slf4j
public class OtpDomainService {
    
    private static final int MAX_OTP_ATTEMPTS = 3;
    private static final int OTP_EXPIRY_MINUTES = 10;
    
    /**
     * Validate if an OTP is still valid based on business rules
     */
    public boolean isOtpValid(OTP otp) {
        if (otp == null) {
            return false;
        }
        
        // Check if OTP has expired
        if (otp.isExpired()) {

            return false;
        }
        
        // Check if maximum attempts reached
        if (otp.isMaxAttemptsReached()) {

            return false;
        }
        
        return true;
    }
    
    /**
     * Check if OTP has reached maximum attempts
     */
    public boolean hasExceededMaxAttempts(OTP otp) {
        return otp != null && otp.getAttempts() >= MAX_OTP_ATTEMPTS;
    }
    
    /**
     * Generate OTP expiry time based on business rules
     */
    public LocalDateTime calculateExpiryTime() {
        return LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);
    }
    
     // Determine if a user should receive a new OTP

    public boolean canReceiveNewOtp(OTP existingOtp) {
        // Business rule: Allow new OTP if no existing valid OTP
        if (existingOtp == null || existingOtp.isExpired()) {
            return true;
        }
        
        // Business rule: Allow new OTP if existing one is close to expiry (within 2 minutes)
        LocalDateTime twoMinutesFromNow = LocalDateTime.now().plusMinutes(2);
        LocalDateTime expiryTime = existingOtp.getCreatedAt().plusMinutes(OTP_EXPIRY_MINUTES);
        return expiryTime.isBefore(twoMinutesFromNow);
    }
    
    /**
     * Validate OTP format and content
     */
    public boolean isValidOtpFormat(String otp) {
        if (otp == null || otp.trim().isEmpty()) {
            return false;
        }
        
        String trimmedOtp = otp.trim();
        
        // Must be exactly 6 digits
        if (trimmedOtp.length() != 6) {
            return false;
        }
        
        // Must contain only digits
        return trimmedOtp.matches("\\d{6}");
    }
}
