package com.wesports.backend.infrastructure.scheduling;

import com.wesports.backend.domain.repository.OTPRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Infrastructure component for OTP cleanup scheduling
 * Handles infrastructure concerns like scheduling
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OtpCleanupScheduler {
    
    private final OTPRepository otpRepository;
    
    /**
     * Clean up expired OTPs every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    @Transactional
    public void cleanupExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        int deletedCount = otpRepository.deleteByExpiresAtBefore(now);
        
        if (deletedCount > 0) {
            log.info("Cleaned up {} expired OTPs", deletedCount);
        }
    }
    
    /**
     * Clean up stale OTPs older than 24 hours
     */
    @Scheduled(cron = "0 0 2 * * *") // Daily at 2 AM
    @Transactional
    public void cleanupStaleOtps() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        int deletedCount = otpRepository.deleteByCreatedAtBefore(cutoff);
        
        if (deletedCount > 0) {
            log.info("Cleaned up {} stale OTPs older than 24 hours", deletedCount);
        }
    }
    
    /**
     * Weekly cleanup of very old OTP records
     */
    @Scheduled(cron = "0 0 3 * * SUN") // Weekly on Sunday at 3 AM
    @Transactional
    public void weeklyDeepCleanup() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        int deletedCount = otpRepository.deleteByCreatedAtBefore(cutoff);
        
        if (deletedCount > 0) {
            log.info("Weekly deep cleanup: removed {} old OTP records", deletedCount);
        }
    }
}
