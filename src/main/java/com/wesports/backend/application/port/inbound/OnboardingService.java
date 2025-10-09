package com.wesports.backend.application.port.inbound;

import com.wesports.backend.application.dto.OnboardingStepResponse;
import com.wesports.backend.application.dto.GenderSelectionRequest;
import com.wesports.backend.application.dto.PositionSelectionRequest;
import com.wesports.backend.application.dto.CategorySelectionRequest;
import com.wesports.backend.application.dto.PlayerProfileRequest;
import com.wesports.backend.domain.valueobject.UserId;

public interface OnboardingService {
    OnboardingStepResponse selectGender(UserId userId, GenderSelectionRequest request);
    OnboardingStepResponse selectPosition(UserId userId, PositionSelectionRequest request);
    OnboardingStepResponse selectCategories(UserId userId, CategorySelectionRequest request);
    OnboardingStepResponse completePlayerProfile(UserId userId, PlayerProfileRequest request);
    OnboardingStepResponse getOnboardingStatus(UserId userId);
}
