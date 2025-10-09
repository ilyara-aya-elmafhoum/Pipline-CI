package com.wesports.backend.infrastructure.security;

import com.wesports.backend.domain.valueobject.UserId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

/**
 * Infrastructure service for access token generation and validation
 * Implements secure JWT access tokens for authenticated users
 */
@Service
public class AccessTokenService implements com.wesports.backend.application.port.outbound.AccessTokenService {

    private static final Logger log = LoggerFactory.getLogger(AccessTokenService.class);

    private final String jwtSecret;
    private final SecretKey signingKey;
    private final Duration accessTokenExpiry;

    public AccessTokenService(
            @Value("${app.jwt.secret:}") String jwtSecret,
            @Value("${app.jwt.access-token-expiry:PT15M}") Duration accessTokenExpiry) {
        this.jwtSecret = initializeSecret(jwtSecret);
        byte[] keyBytes = Base64.getDecoder().decode(this.jwtSecret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpiry = accessTokenExpiry;
        log.info("Access Token Service initialized with {}min expiry", accessTokenExpiry.toMinutes());
    }

    /**
     * Generate an access token for authenticated user (15 minutes default)
     */
    public String generateAccessToken(String userId, String email) {
        try {
            long now = System.currentTimeMillis();
            Date issuedAt = new Date(now);
            Date expiration = new Date(now + accessTokenExpiry.toMillis());

            String token = Jwts.builder()
                    .subject(userId)
                    .audience().add("access").and()
                    .id(UUID.randomUUID().toString())
                    .claim("email", email)
                    .claim("type", "access")
                    .issuedAt(issuedAt)
                    .expiration(expiration)
                    .issuer("ilyara-auth")
                    .signWith(signingKey)
                    .compact();

            log.debug("Generated access token for userId: {} email: {}", userId, email);
            return token;

        } catch (Exception e) {
            log.error("Failed to generate access token for userId: {}", userId, e);
            throw new RuntimeException("Failed to generate access token", e);
        }
    }

    /**
     * Generate an access token for authenticated user (legacy method with UserId)
     */
    public String generateAccessToken(UserId userId, String email) {
        return generateAccessToken(userId.getValue().toString(), email);
    }

    /**
     * Validate and parse access token
     * Returns claims if valid, null if invalid/expired
     */
    public Claims validateAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
                    
            // Verify it's an access token
            if (!"access".equals(claims.get("type"))) {
                log.debug("Token validation failed: not an access token");
                return null;
            }
            
            return claims;
        } catch (Exception e) {
            log.debug("Access token validation failed: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get access token expiry in seconds
     */
    public long getExpiryInSeconds() {
        return accessTokenExpiry.getSeconds();
    }

    /**
     * Extract user ID from access token
     */
    public String extractUserId(String token) {
        Claims claims = validateAccessToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * Extract email from access token
     */
    public String extractEmail(String token) {
        Claims claims = validateAccessToken(token);
        return claims != null ? (String) claims.get("email") : null;
    }

    /**
     * Check if token is expired
     */
    @Override
    public boolean isTokenExpired(String token) {
        Claims claims = validateAccessToken(token);
        if (claims == null) return true;
        
        Date expiration = claims.getExpiration();
        return expiration != null && expiration.before(new Date());
    }

    /**
     * Get token expiration time
     */
    @Override
    public LocalDateTime getTokenExpiration(String token) {
        Claims claims = validateAccessToken(token);
        if (claims == null) return null;
        
        Date expiration = claims.getExpiration();
        return expiration != null ? 
            expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() : null;
    }

    /**
     * Initialize JWT secret from environment or generate secure default
     */
    private String initializeSecret(String providedSecret) {
        if (providedSecret != null && !providedSecret.trim().isEmpty()) {
            log.info("Using JWT secret from configuration for access tokens");
            return providedSecret;
        }

        // Generate a secure random secret for development (must be at least 256 bits for HS256)
        String defaultSecret = Base64.getEncoder().encodeToString(
            "ilyara-auth-access-token-secret-key-for-development-only-change-in-production".getBytes()
        );
        
        log.warn("No JWT_SECRET environment variable set for access tokens. Using default secret. " +
                "Set JWT_SECRET environment variable in production!");
        
        return defaultSecret;
    }
}
