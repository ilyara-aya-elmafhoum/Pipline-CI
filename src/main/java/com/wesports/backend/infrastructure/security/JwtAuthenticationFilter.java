package com.wesports.backend.infrastructure.security;

import com.wesports.backend.application.port.outbound.AccessTokenService;
import com.wesports.backend.domain.model.User;
import com.wesports.backend.domain.repository.UserRepository;
import com.wesports.backend.domain.valueobject.UserId;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JWT Authentication Filter for Ilyara Backend
 * 
 * This filter intercepts all HTTP requests and attempts to authenticate users
 * based on JWT tokens provided either in:
 * 1. Authorization header (Bearer token) - for mobile/API clients
 * 2. HTTP-only cookies - for web browser clients
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AccessTokenService accessTokenService;
    private final UserRepository userRepository;

    /**
     * List of endpoints that should skip JWT authentication
     */
    private static final List<String> EXCLUDED_PATHS = List.of(
        "/api/auth/register/start",
        "/api/auth/register/verify-otp", 
        "/api/auth/login",
        "/api/auth/refresh",  // Token refresh endpoint
        "/api/auth/register/status",
        "/api/onboarding/positions",
        "/api/onboarding/categories",
        "/api/health",
        "/api/test",
        "/api-tester.html",
        "/database-guide.html",
        // Swagger/OpenAPI endpoints
        "/v3/api-docs",
        "/v3/api-docs.yaml",
        "/swagger-ui.html",
        "/swagger-ui/index.html"
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Skip authentication for excluded paths
        String requestPath = request.getRequestURI();

        log.info("Processing request: {}", requestPath);
        
        if (shouldSkipAuthentication(requestPath)) {
            log.info("Skipping JWT authentication for excluded path: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        // Skip if already authenticated
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            log.info("User already authenticated, skipping JWT filter for: {}", requestPath);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract JWT token from request
            String jwt = extractJwtToken(request);
            
            if (jwt != null) {
                log.info("JWT token found, attempting authentication for path: {}", requestPath);
                log.info("Token length: {}, starts with: {}", jwt.length(), jwt.substring(0, Math.min(20, jwt.length())));
                authenticateUser(jwt, request);
                
                // Check if authentication was successful
                if (SecurityContextHolder.getContext().getAuthentication() != null) {
                    log.info("Authentication successful for path: {}", requestPath);
                } else {
                    log.warn("Authentication failed - SecurityContext is still empty for path: {}", requestPath);
                }
            } else {
                log.warn("No JWT token found for protected path: {}", requestPath);
                

            }
        } catch (Exception e) {
            log.error("JWT authentication failed for {}: {}", requestPath, e.getMessage(), e);
            // Clear any partial authentication
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from HTTP request
     * Supports both Authorization header and cookies
     * 
     * Priority:
     * 1. Authorization header (Bearer token) - mobile/API clients
     * 2. accessToken cookie - web clients
     * 3. jwt cookie - legacy compatibility
     */
    private String extractJwtToken(HttpServletRequest request) {
        // 1. Check Authorization header (mobile/API clients)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("JWT token extracted from Authorization header, length: {}, starts with: {}", 
                token.length(), token.substring(0, Math.min(20, token.length())));
            return token;
        }

        // 2. Check cookies (web clients)
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            log.info("Checking {} cookies for JWT token", cookies.length);
            for (Cookie cookie : cookies) {
                log.info("Cookie found: {} = {} (length: {})", 
                    cookie.getName(), 
                    cookie.getValue().substring(0, Math.min(20, cookie.getValue().length())), 
                    cookie.getValue().length());
                
                // Current implementation uses 'accessToken'
                if ("accessToken".equals(cookie.getName())) {
                    log.info("JWT token extracted from accessToken cookie, length: {}, starts with: {}", 
                        cookie.getValue().length(), 
                        cookie.getValue().substring(0, Math.min(20, cookie.getValue().length())));
                    return cookie.getValue();
                }
                // Legacy compatibility with old backend
                if ("jwt".equals(cookie.getName())) {
                    log.info("JWT token extracted from legacy jwt cookie, length: {}, starts with: {}", 
                        cookie.getValue().length(), 
                        cookie.getValue().substring(0, Math.min(20, cookie.getValue().length())));
                    return cookie.getValue();
                }
            }
        } else {
            log.warn("No cookies found in request");
        }


        return null;
    }

    /**
     * Authenticate user using JWT token and set Spring Security context
     */
    private void authenticateUser(String jwt, HttpServletRequest request) {
        try {

            log.info("JWT token to validate: {} (length: {})", 
                jwt.substring(0, Math.min(50, jwt.length())), jwt.length());
            
            // Validate token using AccessTokenService
            try {
                if (accessTokenService.validateAccessToken(jwt) == null) {
                    log.warn("JWT token validation failed - token is invalid or expired");
                    log.warn("Token details: starts with '{}', ends with '{}'", 
                        jwt.substring(0, Math.min(20, jwt.length())), 
                        jwt.substring(Math.max(0, jwt.length() - 20)));
                    return;
                }

            } catch (Exception e) {
                log.error("JWT token validation threw exception: {}", e.getMessage(), e);
                return;
            }

            // Extract user ID from token
            String userIdString = accessTokenService.extractUserId(jwt);
            if (userIdString == null || userIdString.isEmpty()) {
                log.warn("JWT token does not contain valid user ID");
                return;
            }
            log.info("Extracted user ID from token: {}", userIdString);

            // Convert to domain UserId
            UserId userId;
            try {
                userId = UserId.of(UUID.fromString(userIdString));
                log.info("Converted to domain UserId: {}", userId.getValue());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid user ID format in JWT token: {}", userIdString);
                return;
            }

            // Load user from repository
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.warn("User not found in database for ID: {}", userId.getValue());
                return;
            }

            User user = userOpt.get();
            log.info("User found in database: {} ({})", user.getEmail().getValue(), user.getId().getValue());
            
            // Extract email for additional validation
            String tokenEmail = accessTokenService.extractEmail(jwt);
            if (tokenEmail != null && !tokenEmail.equals(user.getEmail().getValue())) {
                log.warn("Email mismatch in JWT token. Token: {}, User: {}", 
                    tokenEmail, user.getEmail().getValue());
                return;
            }
            log.info("Email validation passed: {}", user.getEmail().getValue());

            // Create authentication token
            UsernamePasswordAuthenticationToken authToken = createAuthenticationToken(user, request);
            
            // Set in security context
            SecurityContextHolder.getContext().setAuthentication(authToken);
            
            log.info("User successfully authenticated and SecurityContext set: {} ({})", 
                user.getEmail().getValue(), user.getId().getValue());

        } catch (Exception e) {
            log.error("Error during JWT authentication: {}", e.getMessage(), e);
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * Create Spring Security authentication token for authenticated user
     */
    private UsernamePasswordAuthenticationToken createAuthenticationToken(User user, HttpServletRequest request) {
        // Create authorities based on user role (if implemented)
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_USER")
        );

        // Create custom user principal with user details
        JwtUserPrincipal userPrincipal = new JwtUserPrincipal(
            user.getId().getValue().toString(),
            user.getEmail().getValue(),
            user.getFirstName(),
            user.getLastName(),
            authorities
        );

        UsernamePasswordAuthenticationToken authToken = 
            new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);
        
        // Set authentication details
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        
        return authToken;
    }

    /**
     * Check if the request path should skip JWT authentication
     */
    private boolean shouldSkipAuthentication(String requestPath) {
        // Exact path matches
        if (EXCLUDED_PATHS.contains(requestPath)) {
            return true;
        }

        // Static resources
        if (requestPath.startsWith("/static/") || 
            requestPath.startsWith("/css/") || 
            requestPath.startsWith("/js/") ||
            requestPath.startsWith("/images/")) {
            return true;
        }

        // Swagger/OpenAPI paths
        if (requestPath.startsWith("/swagger-ui/") ||
            requestPath.startsWith("/v3/api-docs") ||
            requestPath.startsWith("/swagger-resources/") ||
            requestPath.startsWith("/webjars/")) {
            return true;
        }

        // Actuator endpoints (if enabled)
        if (requestPath.startsWith("/actuator/")) {
            return true;
        }

        return false;
    }

    /**
     * Custom user principal for JWT authenticated users
     * Contains user information extracted from JWT token and database
     */
    public static class JwtUserPrincipal {
        private final String userId;
        private final String email;
        private final String firstName;
        private final String lastName;
        private final List<SimpleGrantedAuthority> authorities;

        public JwtUserPrincipal(String userId, String email, String firstName, 
                               String lastName, List<SimpleGrantedAuthority> authorities) {
            this.userId = userId;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.authorities = authorities;
        }

        // TODO : lombok
        // Getters
        public String getUserId() { return userId; }
        public String getEmail() { return email; }
        public String getFirstName() { return firstName; }
        public String lastName() { return lastName; }
        public List<SimpleGrantedAuthority> getAuthorities() { return authorities; }

        @Override
        public String toString() {
            return String.format("JwtUserPrincipal{userId='%s', email='%s'}", userId, email);
        }
    }
}
