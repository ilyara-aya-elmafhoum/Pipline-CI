package com.wesports.backend.application.service;

import com.wesports.backend.application.dto.*;
import com.wesports.backend.application.port.inbound.LoginService;
import com.wesports.backend.domain.valueobject.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Application service for authentication operations
 * Orchestrates business logic and handles cross-cutting concerns
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthApplicationService {
    
    private final LoginService loginService;
    
    /**
     * Handle token refresh
     */
    public AuthResponse refreshToken(String refreshToken) {
        try {
            log.info("Processing token refresh request");
            
            if (refreshToken == null || refreshToken.isEmpty()) {
                log.warn("No refresh token provided");
                return AuthResponse.error("Refresh token is required");
            }
            
            // Use LoginService to handle refresh token logic
            return loginService.refreshToken(refreshToken);
            
        } catch (Exception e) {
            log.error("Token refresh failed", e);
            return AuthResponse.error("Failed to refresh token");
        }
    }
    
    /**
     * Handle role selection during registration
     */
    public RegistrationStepResponse selectRole(UserId userId, RoleSelectionRequest request) {
        try {
            // TODO: Implement role selection in domain service
            // For now, return success response
            
            log.info("Role selection completed for user: {}", userId.getValue());
            return RegistrationStepResponse.success(
                "Role selected successfully. Please select your gender.",
                "SELECT_GENDER"
            );
            
        } catch (Exception e) {
            log.error("Role selection failed for user: {}", userId.getValue(), e);
            return RegistrationStepResponse.error("Failed to select role");
        }
    }
    
    /**
     * Get registration status by email
     */
    public RegistrationStatusResponse getRegistrationStatus(String email) {
        try {
            log.info("Retrieving registration status for email: {}", email);

            // TODO: Implement registration status retrieval from domain service
            // For now, return default response
            RegistrationStatusResponse response = new RegistrationStatusResponse(
                email,
                "IN_PROGRESS", 
                "VERIFY_OTP",
                java.util.List.of("OTP_SENT"),
                false
            );
            
            log.info("Registration status retrieved for email: {}", email);
            return response;
            
        } catch (Exception e) {
            log.error("Failed to get registration status for email: {}", email, e);
            return new RegistrationStatusResponse(email, "ERROR", null, null, false);
        }
    }
    
    /**
     * Handle logout
     */
    public LogoutResponse logout(UserId userId, String refreshToken) {
        try {
            log.info("Processing logout for user: {}", userId.getValue());
            
            // Call domain service for logout (takes only refreshToken)
            LogoutResponse response = loginService.logout(refreshToken);
            
            log.info("Logout completed for user: {}", userId.getValue());
            return response;
            
        } catch (Exception e) {
            log.error("Logout failed for user: {}", userId.getValue(), e);
            return LogoutResponse.error("Failed to logout");
        }
    }
}
