package com.wesports.backend.infrastructure.web;

import com.wesports.backend.application.dto.AuthResponse;
import com.wesports.backend.application.dto.EmailRegistrationRequest;
import com.wesports.backend.application.dto.OtpVerificationRequest;
import com.wesports.backend.application.dto.PasswordSetupRequest;
import com.wesports.backend.application.dto.RoleSelectionRequest;
import com.wesports.backend.application.dto.ProfileFormRequest;
import com.wesports.backend.application.dto.RegistrationStepResponse;
import com.wesports.backend.application.port.RegistrationService;
import com.wesports.backend.application.service.AuthenticationContextService;
import com.wesports.backend.domain.valueobject.UserId;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final ClientDetectionService clientDetectionService;
    private final AuthenticationContextService authenticationContextService;

    private static final String REGISTRATION_TOKEN_COOKIE = "reg_token";
    private static final int COOKIE_MAX_AGE_SECONDS = 300; // 5 minutes

    /**
     * Start email registration by sending OTP to provided email
     * POST /api/auth/register/start
     */
    @PostMapping("/register/start")
    public ResponseEntity<RegistrationStepResponse> startEmailRegistration(
            @Valid @RequestBody EmailRegistrationRequest request) {
        
        log.info("Starting email registration for: {}", request.email());
        
        RegistrationStepResponse response = registrationService.startEmailRegistration(request);
        
        if ("error".equals(response.status())) {
            log.warn("Registration start failed for {}: {}", request.email(), response.message());
            return ResponseEntity.badRequest().body(response);
        }
        
        log.info("Registration OTP sent successfully to: {}", request.email());
        return ResponseEntity.ok(response);
    }

    /**
     * Verify the OTP sent during registration
     * POST /api/auth/register/verify-otp
     */
    @PostMapping("/register/verify-otp")
    public ResponseEntity<RegistrationStepResponse> verifyRegistrationOtp(
            @Valid @RequestBody OtpVerificationRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        
        log.info("Verifying registration OTP for: {}", request.email());
        
        // Detect client type for response handling
        String clientType = clientDetectionService.getClientType(httpRequest);
        boolean isMobileClient = clientDetectionService.isMobileClient(httpRequest);
        
        log.info("Client type detected: {} for email: {}", clientType, request.email());
        
        RegistrationStepResponse response = registrationService.verifyRegistrationOtp(request);
        
        if ("error".equals(response.status())) {
            log.warn("OTP verification failed for {}: {}", request.email(), response.message());
            return ResponseEntity.badRequest().body(response);
        }
        
        // Always return JWT token in response for now
        if (response.registrationToken() != null) {
            // Also set cookie for web clients (dual approach)
            if (!isMobileClient) {
                setRegistrationTokenCookie(httpResponse, response.registrationToken());
                log.info("Set registration token cookie for web client: {}", request.email());
            }
            
            // Always return token in JSON response regardless of client type
            log.info("Returning JWT token in response for client type {}: {}", clientType, request.email());
            return ResponseEntity.ok(response);
        }
        
        log.info("OTP verification completed successfully for: {}", request.email());
        return ResponseEntity.ok(response);
    }

    /**
     * Complete registration by setting up password with validated registration token
     * POST /api/auth/register/setup-password
     */
    @PostMapping("/register/setup-password")
    public ResponseEntity<AuthResponse> setupPassword(
            @Valid @RequestBody PasswordSetupRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        
        log.info("Setting up password for registration");
        
        AuthResponse response = registrationService.setupPassword(request);
        
        if ("error".equals(response.status())) {
            log.warn("Password setup failed: {}", response.message());
            return ResponseEntity.badRequest().body(response);
        }
        
        // Detect client type for response handling
        boolean isMobileClient = clientDetectionService.isMobileClient(httpRequest);
        
        // Set refresh token as HttpOnly cookie for web clients
        if (!isMobileClient && response.refreshToken() != null) {
            setRefreshTokenCookie(httpResponse, response.refreshToken());
        }
        
        // Set access token as HttpOnly cookie for web clients
        if (!isMobileClient && response.accessToken() != null) {
            setAccessTokenCookie(httpResponse, response.accessToken());
        }
        
        if (!isMobileClient) {
            // Remove tokens from response for web clients (they're in cookies)
            AuthResponse webResponse = new AuthResponse(
                response.status(),
                response.message(),
                null, // No access token in response for web clients
                null, // No refresh token in response for web clients
                response.expiresIn(),
                response.user()
            );
            
            log.info("Registration completed successfully for web client, refresh token set in cookie");
            return ResponseEntity.ok(webResponse);
        }
        
        // Mobile clients get both tokens in response
        log.info("Registration completed successfully for mobile client");
        return ResponseEntity.ok(response);
    }

    /**
     * Role selection step - user chooses their role (player, agent, coach, etc.)
     * POST /api/auth/register/select-role
     */
    @PostMapping("/register/select-role")
    public ResponseEntity<RegistrationStepResponse> selectRole(
            @Valid @RequestBody RoleSelectionRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Role selection for user");
        
        try {
            UserId userId = authenticationContextService.getAuthenticatedUserId(httpRequest);
            RegistrationStepResponse response = registrationService.selectRole(userId, request);
            
            if ("error".equals(response.status())) {
                log.warn("Role selection failed: {}", response.message());
                return ResponseEntity.badRequest().body(response);
            }
            
            log.info("Role selected successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.warn("Role selection failed - authentication error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                RegistrationStepResponse.error("Authentication required. Please login again.")
            );
        }
    }

    /**
     * Profile form step - user provides personal details
     * POST /api/auth/register/profile-form
     */
    @PostMapping("/register/profile-form")
    public ResponseEntity<RegistrationStepResponse> submitProfileForm(
            @Valid @RequestBody ProfileFormRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Profile form submission for user");
        
        try {
            UserId userId = authenticationContextService.getAuthenticatedUserId(httpRequest);
            RegistrationStepResponse response = registrationService.submitProfileForm(userId, request);
            
            if ("error".equals(response.status())) {
                log.warn("Profile form failed: {}", response.message());
                return ResponseEntity.badRequest().body(response);
            }
            
            log.info("Profile form submitted successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.warn("Profile form failed - authentication error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                RegistrationStepResponse.error("Authentication required. Please login again.")
            );
        }
    }

        /**
     * Set registration token as HttpOnly secure cookie
     */
    private void setRegistrationTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(REGISTRATION_TOKEN_COOKIE, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(COOKIE_MAX_AGE_SECONDS);
        
        response.addCookie(cookie);
        // Also set via header for better SameSite support
        response.setHeader("Set-Cookie", 
            String.format("%s=%s; Path=/api/auth; Max-Age=%d; HttpOnly; Secure; SameSite=Lax", 
                REGISTRATION_TOKEN_COOKIE, token, COOKIE_MAX_AGE_SECONDS));
        
        log.debug("Set registration token cookie");
    }

    /**
     * Set refresh token as HttpOnly secure cookie (30 days)
     */
    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
        
        response.addCookie(cookie);
        // Also set via header for better SameSite support
        response.setHeader("Set-Cookie", 
            String.format("refresh_token=%s; Path=/api/auth; Max-Age=%d; HttpOnly; Secure; SameSite=Lax", 
                refreshToken, 30 * 24 * 60 * 60));
        
        log.debug("Set refresh token cookie");
    }

    /**
     * Set access token as HttpOnly secure cookie (15 minutes)
     */
    private void setAccessTokenCookie(HttpServletResponse response, String accessToken) {
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(15 * 60); // 15 minutes
        
        response.addCookie(cookie);
        // Also set via header for better SameSite support
        response.setHeader("Set-Cookie", 
            String.format("accessToken=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=Lax", 
                accessToken, 15 * 60));
        
        log.debug("Set access token cookie");
    }
}
