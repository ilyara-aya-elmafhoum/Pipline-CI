package com.wesports.backend.application.service;

import com.wesports.backend.application.port.outbound.AccessTokenService;
import com.wesports.backend.domain.model.User;
import com.wesports.backend.domain.valueobject.UserId;
import com.wesports.backend.infrastructure.security.JwtAuthenticationFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Authentication Context Service for Ilyara Backend
 * 
 * Provides authentication context management in hexagonal architecture.
 * Works with JWT authentication filter to extract user information from:
 * 1. Spring Security context (after JWT filter processes the request)
 * 2. Direct JWT token extraction (fallback for backward compatibility)
 * 
 * Inspired by old backend patterns but adapted for current hexagonal architecture.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationContextService {
    
    private final AccessTokenService accessTokenService;
    private final UserQueryService userQueryService;

    /**
     * Get authenticated user from Spring Security context (preferred method)
     * This works after JWT filter has processed the request
     */
    public User getAuthenticatedUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found in security context");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof JwtAuthenticationFilter.JwtUserPrincipal) {
            JwtAuthenticationFilter.JwtUserPrincipal userPrincipal = 
                (JwtAuthenticationFilter.JwtUserPrincipal) principal;
            
            UserId userId = UserId.of(UUID.fromString(userPrincipal.getUserId()));
            Optional<User> userOpt = userQueryService.findById(userId);
            
            if (userOpt.isEmpty()) {
                log.warn("User not found for authenticated principal: {}", userPrincipal.getUserId());
                throw new RuntimeException("Authenticated user not found in database");
            }
            
            return userOpt.get();
        }
        
        throw new RuntimeException("Invalid authentication principal type");
    }

    /**
     * Get authenticated user ID from Spring Security context
     */
    public UserId getAuthenticatedUserIdFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found in security context");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof JwtAuthenticationFilter.JwtUserPrincipal) {
            JwtAuthenticationFilter.JwtUserPrincipal userPrincipal = 
                (JwtAuthenticationFilter.JwtUserPrincipal) principal;
            return UserId.of(UUID.fromString(userPrincipal.getUserId()));
        }
        
        throw new RuntimeException("Invalid authentication principal type");
    }

    /**
     * Get authenticated user from HTTP request
     */
    public User getAuthenticatedUser(HttpServletRequest request) {
        // First try to get from security context (preferred)
        try {
            return getAuthenticatedUserFromContext();
        } catch (Exception e) {
        }

        // Fallback to direct token extraction
        return getAuthenticatedUserFromToken(request);
    }

    /**
     * Get authenticated user ID from HTTP request
     * TODO : remove this
     */
    public UserId getAuthenticatedUserId(HttpServletRequest request) {
        // First try to get from security context (preferred)
        try {
            return getAuthenticatedUserIdFromContext();
        } catch (Exception e) {
            log.debug("No user in security context, falling back to direct token extraction");
        }

        // Fallback to direct token extraction
        return getAuthenticatedUserFromToken(request).getId();
    }

    /**
     * Direct token extraction method (fallback)
     * TODO : remove this
     */
    private User getAuthenticatedUserFromToken(HttpServletRequest request) {
        String token = extractToken(request);
        
        if (accessTokenService.validateAccessToken(token) == null) {
            log.warn("Invalid or expired token provided");
            throw new RuntimeException("Invalid or expired token");
        }
        
        String userIdString = accessTokenService.extractUserId(token);
        if (userIdString == null) {
            log.warn("Unable to extract user ID from token");
            throw new RuntimeException("Invalid token format");
        }
        
        UserId userId = UserId.of(UUID.fromString(userIdString));
        Optional<User> userOpt = userQueryService.findById(userId);
        
        if (userOpt.isEmpty()) {
            log.warn("User not found for ID: {}", userIdString);
            throw new RuntimeException("User not found");
        }
        
        User user = userOpt.get();
        log.debug("Successfully authenticated user: {}", user.getEmail().getValue());
        return user;
    }

    /**
     * Check if request contains valid authentication
     */
    public boolean isRequestAuthenticated(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            return accessTokenService.validateAccessToken(token) != null;
        } catch (Exception e) {
            log.debug("Request authentication failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract JWT token from request
     * First tries cookies (for web clients), then Authorization header (for mobile)
     * Same logic as old repository
     */
    public String extractToken(HttpServletRequest request) {
        // Try cookie first (for web clients)
        String tokenFromCookie = getTokenFromCookies(request);
        if (tokenFromCookie != null) {
            log.debug("Using access token from cookie");
            return tokenFromCookie;
        }
        
        // Try Authorization header (for mobile clients)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            return authHeader.substring(7);
        }
        
        throw new RuntimeException("No valid authorization token provided");
    }
    
    /**
     * Extract access token from cookies
     *  Todo : remove this
     */
    private String getTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
