package com.wesports.backend.infrastructure.web;

import com.wesports.backend.application.dto.LanguageResponse;
import com.wesports.backend.application.dto.LanguageSelectionRequest;
import com.wesports.backend.application.service.LanguageService;
import com.wesports.backend.application.service.AuthenticationContextService;
import com.wesports.backend.domain.valueobject.UserId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TEMPORARY: REST controller for language management
 * TODO: Replace with proper language management endpoints once full system is implemented
 */
@Slf4j
@RestController
@RequestMapping("/api/languages")
@RequiredArgsConstructor
public class LanguageController {
    
    private final LanguageService languageService;
    private final AuthenticationContextService authenticationContextService;

    /**
     * Get all active languages
     * GET /api/languages
     */
    @GetMapping
    public ResponseEntity<List<LanguageResponse>> getActiveLanguages() {
        log.info("Fetching active languages");
        List<LanguageResponse> languages = languageService.getActiveLanguages();
        return ResponseEntity.ok(languages);
    }

    /**
     * Update user's language preference
     * PUT /api/languages/preference
     */
    @PutMapping("/preference")
    public ResponseEntity<String> updateLanguagePreference(
            @Valid @RequestBody LanguageSelectionRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("Updating language preference to: {}", request.languageCode());
        
        try {
            UserId userId = authenticationContextService.getAuthenticatedUserId(httpRequest);
            boolean success = languageService.updateUserLanguage(userId, request.languageCode());
            
            if (success) {
                log.info("Language preference updated successfully");
                return ResponseEntity.ok("Language preference updated successfully");
            } else {
                log.warn("Failed to update language preference");
                return ResponseEntity.badRequest().body("Invalid language code or user not found");
            }
            
        } catch (Exception e) {
            log.warn("Language preference update failed - authentication error: {}", e.getMessage());
            return ResponseEntity.status(401).body("Authentication required");
        }
    }
}
