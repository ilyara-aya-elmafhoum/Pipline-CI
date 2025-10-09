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
import java.util.Base64;
import java.util.Date;

/**
 * Infrastructure service for JWT token generation and validation
 * Implements secure token handling for registration flow
 * Compatible with JJWT 0.12.3
 */
@Service
public class JwtTokenService {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenService.class);

    private final String jwtSecret;
    private final SecretKey signingKey;

    public JwtTokenService(@Value("${app.jwt.secret:}") String jwtSecret) {
        this.jwtSecret = initializeSecret(jwtSecret);
        byte[] keyBytes = Base64.getDecoder().decode(this.jwtSecret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        log.info("JWT Token Service initialized with secure signing key");
    }

    /**
     * Generate a short-lived registration JWT token (5 minutes)
     * Used for secure registration flow after email verification
     */
    public String generateRegistrationToken(UserId userId, String jti) {
        try {
            long now = System.currentTimeMillis();
            Date issuedAt = new Date(now);
            Date expiration = new Date(now + Duration.ofMinutes(5).toMillis());

            String token = Jwts.builder()
                    .subject(userId.getValue().toString())
                    .audience().add("registration").and()
                    .id(jti)
                    .issuedAt(issuedAt)
                    .expiration(expiration)
                    .issuer("ilyara-auth")
                    .signWith(signingKey)
                    .compact();

            log.debug("Generated registration token for userId: {} with jti: {}", userId.getValue(), jti);
            return token;

        } catch (Exception e) {
            log.error("Failed to generate registration token for userId: {}", userId.getValue(), e);
            throw new RuntimeException("Failed to generate registration token", e);
        }
    }

    /**
     * Validate and parse JWT token
     * Returns claims if valid, null if invalid/expired
     */
    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract JWT ID from token
     */
    public String extractJti(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.getId() : null;
    }

    /**
     * Extract subject (userId) from token
     */
    public String extractSubject(String token) {
        Claims claims = validateToken(token);
        return claims != null ? claims.getSubject() : null;
    }

    /**
     * Check if token is for registration audience
     */
    public boolean isRegistrationToken(String token) {
        Claims claims = validateToken(token);
        return claims != null && claims.getAudience() != null && 
               claims.getAudience().contains("registration");
    }

    /**
     * Initialize JWT secret from environment or generate secure default
     */
    private String initializeSecret(String providedSecret) {
        if (providedSecret != null && !providedSecret.trim().isEmpty()) {
            log.info("Using JWT secret from configuration");
            return providedSecret;
        }

        // Generate a secure random secret for development (must be at least 256 bits for HS256)
        String defaultSecret = Base64.getEncoder().encodeToString(
            "ilyara-auth-default-secret-key-for-development-only-change-in-production-2024".getBytes()
        );
        
        log.warn("No JWT_SECRET environment variable set. Using default secret. " +
                "Set JWT_SECRET environment variable in production!");
        
        return defaultSecret;
    }
}
