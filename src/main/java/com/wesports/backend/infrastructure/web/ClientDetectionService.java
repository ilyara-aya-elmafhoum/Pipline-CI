package com.wesports.backend.infrastructure.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

// TODO : this file will be removed the request / response will be same for all clients type
/**
 * Infrastructure service for detecting client type and managing response format
 * Helps determine whether to use cookies (web) or JSON tokens (mobile/API)
 */
@Slf4j
@Service
public class ClientDetectionService {

    private static final String CLIENT_TYPE_HEADER = "X-Client-Type";
    private static final String USER_AGENT_HEADER = "User-Agent";

    /**
     * Determine if the client is a mobile application
     * Based on headers and user agent
     */
    public boolean isMobileClient(HttpServletRequest request) {
        // Check explicit client type header
        String clientType = request.getHeader(CLIENT_TYPE_HEADER);
        if ("mobile".equalsIgnoreCase(clientType)) {
            log.debug("Mobile client detected via X-Client-Type header");
            return true;
        }

        // Check User-Agent for mobile patterns
        String userAgent = request.getHeader(USER_AGENT_HEADER);
        if (userAgent != null) {
            String lowerUserAgent = userAgent.toLowerCase();
            
            // Check for mobile app patterns
            if (lowerUserAgent.contains("ilyara-mobile") || 
                lowerUserAgent.contains("ilyara-app") ||
                lowerUserAgent.contains("flutter") ||
                lowerUserAgent.contains("dart") ||
                lowerUserAgent.contains("react-native")) {
                log.debug("Mobile client detected via User-Agent: {}", userAgent);
                return true;
            }
        }

        // Default to web client
        log.debug("Web client detected");
        return false;
    }

    /**
     * Check if client supports cookies
     * Mobile clients typically prefer JSON tokens
     */
    public boolean supportsCookies(HttpServletRequest request) {
        return !isMobileClient(request);
    }

    /**
     * Get client type for logging
     */
    public String getClientType(HttpServletRequest request) {
        return isMobileClient(request) ? "mobile" : "web";
    }
    
    /**
     * Detect the client type - returns "web" for browser clients, "mobile" for mobile apps, "api" for other clients
     * Alias for getClientType for compatibility
     */
    public String detectClientType(HttpServletRequest request) {
        return getClientType(request);
    }
}
