package com.wesports.backend.application.service;

import com.wesports.backend.application.dto.OnboardingStepResponse;
import com.wesports.backend.application.dto.GenderSelectionRequest;
import com.wesports.backend.application.dto.PositionSelectionRequest;
import com.wesports.backend.application.dto.CategorySelectionRequest;
import com.wesports.backend.application.dto.PlayerProfileRequest;
import com.wesports.backend.application.port.inbound.OnboardingService;
import com.wesports.backend.domain.valueobject.UserId;
import com.wesports.backend.domain.valueobject.Position;
import com.wesports.backend.domain.valueobject.Category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Application service for onboarding operations
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OnboardingApplicationService {
    
    private final OnboardingService onboardingService;
    
    /**
     * Get user's onboarding status
     */
    public OnboardingStepResponse getOnboardingStatus(UserId userId) {
        log.info("Getting onboarding status for user: {}", userId.getValue());
        
        OnboardingStepResponse response = onboardingService.getOnboardingStatus(userId);
        
        log.info("Onboarding status retrieved for user: {} - Current step: {}", 
                userId.getValue(), response.nextStep());
        return response;
    }
    
    /**
     * Handle gender selection in onboarding flow
     */
    public OnboardingStepResponse selectGender(UserId userId, GenderSelectionRequest request) {
        log.info("Processing gender selection for user: {} - Gender: {}", 
                userId.getValue(), request.gender());
        
        OnboardingStepResponse response = onboardingService.selectGender(userId, request);
        
        log.info("Gender selection completed for user: {} - Next step: {}", 
                userId.getValue(), response.nextStep());
        return response;
    }
    
    /**
     * Handle position selection in onboarding flow
     */
    public OnboardingStepResponse selectPosition(UserId userId, PositionSelectionRequest request) {
        log.info("Processing position selection for user: {} - Position: {}", 
                userId.getValue(), request.positionCode());
        
        OnboardingStepResponse response = onboardingService.selectPosition(userId, request);
        
        log.info("Position selection completed for user: {} - Next step: {}", 
                userId.getValue(), response.nextStep());
        return response;
    }
    
    /**
     * Handle category selection in onboarding flow
     */
    public OnboardingStepResponse selectCategories(UserId userId, CategorySelectionRequest request) {
        log.info("Processing category selection for user: {} - Categories: {}", 
                userId.getValue(), request.categoryCodes());
        
        OnboardingStepResponse response = onboardingService.selectCategories(userId, request);
        
        log.info("Category selection completed for user: {} - Next step: {}", 
                userId.getValue(), response.nextStep());
        return response;
    }
    
    /**
     * Handle profile completion in onboarding flow
     */
    public OnboardingStepResponse completePlayerProfile(UserId userId, PlayerProfileRequest request) {

        
        OnboardingStepResponse response = onboardingService.completePlayerProfile(userId, request);
        
        log.info("Profile completion finished for user: {} - Status: {}", 
                userId.getValue(), response.status());
        return response;
    }
    
    /**
     * Get available positions for selection
     */
    public List<Map<String, String>> getAvailablePositions() {

        
        return Arrays.stream(Position.values())
                .map(position -> Map.of(
                    "code", position.name(),
                    "displayName", position.getDisplayName(),
                    "category", getPositionCategory(position)
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * Get available categories for selection
     */
    public List<Map<String, String>> getAvailableCategories() {

        
        return Arrays.stream(Category.values())
                .map(category -> Map.of(
                    "code", category.name(),
                    "displayName", category.getDisplayName()
                ))
                .collect(Collectors.toList());
    }
    
    /**
     * Helper method to categorize positions
     */
    private String getPositionCategory(Position position) {
        switch (position) {
            case GK: return "Goalkeeper";
            case CB, LB, RB, LWB, RWB: return "Defender";
            case CDM, CM, CAM, LM, RM: return "Midfielder";
            case LW, RW, ST, CF, LF, RF: return "Forward";
            default: return "Unknown";
        }
    }
}
