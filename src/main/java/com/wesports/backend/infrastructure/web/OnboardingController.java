package com.wesports.backend.infrastructure.web;

import com.wesports.backend.application.dto.OnboardingStepResponse;
import com.wesports.backend.application.dto.GenderSelectionRequest;
import com.wesports.backend.application.dto.PositionSelectionRequest;
import com.wesports.backend.application.dto.CategorySelectionRequest;
import com.wesports.backend.application.dto.PlayerProfileRequest;
import com.wesports.backend.application.service.OnboardingApplicationService;
import com.wesports.backend.application.service.AuthenticationContextService;
import com.wesports.backend.domain.valueobject.UserId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * REST controller for onboarding operations
 * Handles HTTP requests and delegates to application service
 * Part of the infrastructure layer in hexagonal architecture
 */
@RestController
@RequestMapping("/api/onboarding")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class OnboardingController {
    
    private static final Logger log = Logger.getLogger(OnboardingController.class.getName());
    
    private final OnboardingApplicationService onboardingApplicationService;
    private final AuthenticationContextService authenticationContextService;
    
    @Autowired
    public OnboardingController(OnboardingApplicationService onboardingApplicationService, 
                              AuthenticationContextService authenticationContextService) {
        this.onboardingApplicationService = onboardingApplicationService;
        this.authenticationContextService = authenticationContextService;
    }
    
        
    @GetMapping("/status")
    public ResponseEntity<OnboardingStepResponse> getOnboardingStatus(HttpServletRequest request) {
        try {
            UserId userId = authenticationContextService.getAuthenticatedUserId(request);
            OnboardingStepResponse response = onboardingApplicationService.getOnboardingStatus(userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warning("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(OnboardingStepResponse.error("Authentication required"));
        } catch (Exception e) {
            log.severe("Error getting onboarding status: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body(OnboardingStepResponse.error("Failed to get onboarding status"));
        }
    }
    
    @PostMapping("/gender")
    public ResponseEntity<OnboardingStepResponse> selectGender(
            @Valid @RequestBody GenderSelectionRequest request,
            HttpServletRequest httpRequest) {
        try {
            UserId userId = authenticationContextService.getAuthenticatedUserId(httpRequest);
            OnboardingStepResponse response = onboardingApplicationService.selectGender(userId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warning("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(OnboardingStepResponse.error("Authentication required"));
        } catch (Exception e) {
            log.severe("Error selecting gender: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body(OnboardingStepResponse.error("Failed to select gender"));
        }
    }
    
    @PostMapping("/position")
    public ResponseEntity<OnboardingStepResponse> selectPosition(
            @Valid @RequestBody PositionSelectionRequest request,
            HttpServletRequest httpRequest) {
        try {
            UserId userId = authenticationContextService.getAuthenticatedUserId(httpRequest);
            OnboardingStepResponse response = onboardingApplicationService.selectPosition(userId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warning("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(OnboardingStepResponse.error("Authentication required"));
        } catch (Exception e) {
            log.severe("Error selecting position: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body(OnboardingStepResponse.error("Failed to select position"));
        }
    }
    
    @PostMapping("/categories")
    public ResponseEntity<OnboardingStepResponse> selectCategories(
            @Valid @RequestBody CategorySelectionRequest request,
            HttpServletRequest httpRequest) {
        try {
            UserId userId = authenticationContextService.getAuthenticatedUserId(httpRequest);
            OnboardingStepResponse response = onboardingApplicationService.selectCategories(userId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warning("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(OnboardingStepResponse.error("Authentication required"));
        } catch (Exception e) {
            log.severe("Error selecting categories: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body(OnboardingStepResponse.error("Failed to select categories"));
        }
    }
    
    @PostMapping("/complete-profile")
    public ResponseEntity<OnboardingStepResponse> completePlayerProfile(
            @Valid @RequestBody PlayerProfileRequest request,
            HttpServletRequest httpRequest) {
        try {
            UserId userId = authenticationContextService.getAuthenticatedUserId(httpRequest);
            OnboardingStepResponse response = onboardingApplicationService.completePlayerProfile(userId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.warning("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(OnboardingStepResponse.error("Authentication required"));
        } catch (Exception e) {
            log.severe("Error completing profile: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body(OnboardingStepResponse.error("Failed to complete profile"));
        }
    }
    
    @GetMapping("/positions")
    public ResponseEntity<List<Map<String, String>>> getAvailablePositions() {
        try {
            List<Map<String, String>> positions = onboardingApplicationService.getAvailablePositions();
            return ResponseEntity.ok(positions);
        } catch (Exception e) {
            log.severe("Error getting positions: " + e.getMessage());
            return ResponseEntity.internalServerError().body(null);
        }
    }
    
    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, String>>> getAvailableCategories() {
        try {
            List<Map<String, String>> categories = onboardingApplicationService.getAvailableCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            log.severe("Error getting categories: " + e.getMessage());
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
