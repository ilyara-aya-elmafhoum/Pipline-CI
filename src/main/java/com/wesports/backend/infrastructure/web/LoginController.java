package com.wesports.backend.infrastructure.web;

import com.wesports.backend.application.dto.AuthResponse;
import com.wesports.backend.application.dto.LoginRequest;
import com.wesports.backend.application.dto.LogoutResponse;
import com.wesports.backend.application.port.inbound.LoginService;
import com.wesports.backend.application.service.AuthApplicationService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LoginController {
    
    private static final Logger log = Logger.getLogger(LoginController.class.getName());
    
    private final LoginService loginService;
    private final ClientDetectionService clientDetectionService;
    private final AuthApplicationService authApplicationService;
    
    @Autowired
    public LoginController(LoginService loginService, ClientDetectionService clientDetectionService, AuthApplicationService authApplicationService) {
        this.loginService = loginService;
        this.clientDetectionService = clientDetectionService;
        this.authApplicationService = authApplicationService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        
        log.info("Login attempt for: " + request.email());
        
        // Detect client type
        String clientType = clientDetectionService.detectClientType(httpRequest);
        log.info("Client type detected: " + clientType + " for email: " + request.email());
        
        // Process login
        AuthResponse response = loginService.login(request);
        
        if ("success".equals(response.status()) && "web".equals(clientType)) {
            // Set HTTP-only cookies for web clients
            if (response.refreshToken() != null) {
                Cookie refreshCookie = new Cookie("refreshToken", response.refreshToken());
                refreshCookie.setHttpOnly(true);
                refreshCookie.setSecure(false); // Set to true in production with HTTPS
                refreshCookie.setPath("/");
                refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
                httpResponse.addCookie(refreshCookie);
                log.info("Set refresh token cookie for web client: " + request.email());
            }
            
            if (response.accessToken() != null) {
                Cookie accessCookie = new Cookie("accessToken", response.accessToken());
                accessCookie.setHttpOnly(true);
                accessCookie.setSecure(false); // Set to true in production with HTTPS
                accessCookie.setPath("/");
                accessCookie.setMaxAge(15 * 60); // 15 minutes
                httpResponse.addCookie(accessCookie);
                log.info("Set access token cookie for web client: " + request.email());
            }
            
            // Return response without tokens for web clients (tokens are in cookies)
            log.info("Returning cookie-based response for web client: " + request.email());
            return ResponseEntity.ok(AuthResponse.successWithUser(response.message(), response.user()));
        }
        
        // Return full response with tokens for mobile/API clients
        log.info("Returning JSON token response for client type " + clientType + ": " + request.email());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        
        log.info("Logout attempt");
        
        // Get refresh token from cookie or request header
        String refreshToken = getRefreshTokenFromRequest(httpRequest);
        
        // Process logout
        LogoutResponse response = loginService.logout(refreshToken);
        
        // Clear cookies for web clients
        String clientType = clientDetectionService.detectClientType(httpRequest);
        if ("web".equals(clientType)) {
            clearAuthCookies(httpResponse);
            log.info("Cleared authentication cookies for web client");
        }
        
        log.info("Logout completed");
        return ResponseEntity.ok(response);
    }
    
    private String getRefreshTokenFromRequest(HttpServletRequest request) {
        // Try to get from cookie first (web clients)
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        // Try to get from Authorization header (mobile/API clients)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        return null;
    }
    
    private void clearAuthCookies(HttpServletResponse response) {
        // Clear refresh token cookie
        Cookie refreshCookie = new Cookie("refreshToken", "");
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);
        response.addCookie(refreshCookie);
        
        // Clear access token cookie
        Cookie accessCookie = new Cookie("accessToken", "");
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        response.addCookie(accessCookie);
    }

    /**
     * Refresh access token using refresh token
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        
        log.info("Token refresh attempt");
        
        // Get refresh token from cookie or request header
        String refreshToken = getRefreshTokenFromRequest(httpRequest);
        
        if (refreshToken == null || refreshToken.isEmpty()) {
            log.warning("No refresh token provided for refresh");
            return ResponseEntity.status(401).body(AuthResponse.error("Refresh token is required"));
        }
        
        // Process token refresh using AuthApplicationService
        AuthResponse response = authApplicationService.refreshToken(refreshToken);
        
        if (!"success".equals(response.status())) {
            log.warning("Token refresh failed: " + response.message());
            return ResponseEntity.status(401).body(response);
        }
        
        // Set new tokens in cookies for web clients
        String clientType = clientDetectionService.detectClientType(httpRequest);
        if ("web".equals(clientType) && response.refreshToken() != null) {
            // Set new refresh token cookie
            Cookie refreshCookie = new Cookie("refreshToken", response.refreshToken());
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(false); // Set to true in production with HTTPS
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            httpResponse.addCookie(refreshCookie);
            
            // Set new access token cookie
            if (response.accessToken() != null) {
                Cookie accessCookie = new Cookie("accessToken", response.accessToken());
                accessCookie.setHttpOnly(true);
                accessCookie.setSecure(false); // Set to true in production with HTTPS
                accessCookie.setPath("/");
                accessCookie.setMaxAge(15 * 60); // 15 minutes
                httpResponse.addCookie(accessCookie);
            }
            
            // Return response without tokens for web clients (tokens are in cookies)
            log.info("Token refreshed successfully for web client");
            return ResponseEntity.ok(AuthResponse.successWithUser(response.message(), response.user()));
        }
        
        // Return full response with tokens for mobile/API clients
        log.info("Token refreshed successfully for client type: " + clientType);
        return ResponseEntity.ok(response);
    }
}
