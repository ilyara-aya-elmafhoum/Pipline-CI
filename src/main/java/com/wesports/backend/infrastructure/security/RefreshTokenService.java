package com.wesports.backend.infrastructure.security;

import com.wesports.backend.domain.valueobject.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

// TODO : i will remove this when i handle the revoking in refresh token jwt
/**
 * Infrastructure service for refresh token generation and management
 * Uses simple secure random tokens (not JWT) for refresh functionality
 * Part of the infrastructure layer in hexagonal architecture
 */
@Service
@org.springframework.context.annotation.Primary
public class RefreshTokenService implements com.wesports.backend.application.port.outbound.RefreshTokenService {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);

    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, TokenInfo> refreshTokenStore = new ConcurrentHashMap<>();
    
    /**
     * Generate a secure random refresh token (not JWT)
     */
    public String generateRefreshToken(UserId userId, String email) {
        try {
            // Generate 32-byte random token
            byte[] tokenBytes = new byte[32];
            secureRandom.nextBytes(tokenBytes);
            String refreshToken = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
            
            // Store token info (in production, use database)
            long expiryTime = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000); // 30 days
            refreshTokenStore.put(refreshToken, new TokenInfo(userId, email, expiryTime));
            
            log.debug("Generated refresh token for userId: {} email: {}", userId.getValue(), email);
            return refreshToken;
            
        } catch (Exception e) {
            log.error("Failed to generate refresh token for userId: {}", userId.getValue(), e);
            throw new RuntimeException("Failed to generate refresh token", e);
        }
    }
    
    /**
     * Validate refresh token and return user info
     */
    public TokenInfo validateRefreshTokenInternal(String refreshToken) {
        try {
            TokenInfo tokenInfo = refreshTokenStore.get(refreshToken);
            
            if (tokenInfo == null) {
                log.debug("Refresh token not found");
                return null;
            }
            
            if (System.currentTimeMillis() > tokenInfo.expiryTime()) {
                log.debug("Refresh token expired");
                refreshTokenStore.remove(refreshToken);
                return null;
            }
            
            return tokenInfo;
            
        } catch (Exception e) {
            log.debug("Refresh token validation failed: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Revoke refresh token
     */
    public void revokeRefreshToken(String refreshToken) {
        refreshTokenStore.remove(refreshToken);
        log.debug("Revoked refresh token");
    }
    
    // Implement interface methods
    
    @Override
    public String generateRefreshToken(String userId, String email) {
        return generateRefreshToken(UserId.of(java.util.UUID.fromString(userId)), email);
    }
    
    @Override
    public boolean validateRefreshToken(String token) {
        return validateRefreshTokenInternal(token) != null;
    }
    
    @Override
    public String extractUserIdFromToken(String token) {
        TokenInfo tokenInfo = validateRefreshTokenInternal(token);
        return tokenInfo != null ? tokenInfo.userId().getValue().toString() : null;
    }
    
    @Override
    public String extractEmailFromToken(String token) {
        TokenInfo tokenInfo = validateRefreshTokenInternal(token);
        return tokenInfo != null ? tokenInfo.email() : null;
    }
    
    @Override
    public boolean isTokenExpired(String token) {
        TokenInfo tokenInfo = refreshTokenStore.get(token);
        if (tokenInfo == null) return true;
        return System.currentTimeMillis() > tokenInfo.expiryTime();
    }
    
    @Override
    public LocalDateTime getTokenExpiration(String token) {
        TokenInfo tokenInfo = refreshTokenStore.get(token);
        if (tokenInfo == null) return null;
        return LocalDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(tokenInfo.expiryTime()),
            ZoneId.systemDefault()
        );
    }
    
    @Override
    public void invalidateRefreshToken(String token) {
        revokeRefreshToken(token);
    }
    
    @Override
    public String generateAccessTokenFromRefreshToken(String refreshToken) {
        // This method should delegate to AccessTokenService
        // For now, return null as it's not needed for current implementation
        throw new UnsupportedOperationException("Use AccessTokenService.generateAccessToken instead");
    }
    
    /**
     * Token information record
     */
    public record TokenInfo(
        UserId userId,
        String email,
        long expiryTime
    ) {}
}
