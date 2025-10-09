package com.wesports.backend.domain.repository;

import com.wesports.backend.domain.model.OTP;
import com.wesports.backend.domain.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OTPRepository {
    
    OTP save(OTP otp);
    
    Optional<OTP> findById(UUID otpId);
    
    Optional<OTP> findByUserIdAndType(UserId userId, String type);
    
    Optional<OTP> findValidOTPByUserIdAndType(UserId userId, String type);
    
    List<OTP> findByUserId(UserId userId);
    
    List<OTP> findExpiredOTPs();
    
    void delete(OTP otp);
    
    void deleteById(UUID otpId);
    
    void deleteByUserId(UserId userId);
    
    void deleteExpiredOTPs();
    
    // Additional cleanup methods for infrastructure concerns
    int deleteByExpiresAtBefore(LocalDateTime cutoff);
    
    int deleteByCreatedAtBefore(LocalDateTime cutoff);
}
