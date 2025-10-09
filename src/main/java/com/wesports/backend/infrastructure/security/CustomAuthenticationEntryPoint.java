package com.wesports.backend.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wesports.backend.application.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Custom authentication entry point to handle unauthorized access
 * Returns standardized error responses following our ErrorResponse format
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    private final ObjectMapper objectMapper;
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
        
        log.warn("Unauthorized access attempt to: {} - {}", request.getRequestURI(), authException.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED.value())
                .status("error")
                .message("Authentication required")
                .code("AUTHENTICATION_REQUIRED")
                .timestamp(LocalDateTime.now().toString())
                .path(request.getRequestURI())
                .build();
        
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
