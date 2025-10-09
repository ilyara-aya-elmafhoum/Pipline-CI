package com.wesports.backend.application.port.inbound;

import com.wesports.backend.application.dto.PlayerProfileResponse;
import com.wesports.backend.domain.valueobject.UserId;

import com.wesports.backend.application.dto.PlayerProfileUpdateRequest;
import com.wesports.backend.application.dto.PlayerProfileUpdateResponse;

public interface PlayerProfileService {
    
    /**
     * Get current player profile data
     * Returns complete player information including user base data and player-specific fields
     */
    PlayerProfileResponse getCurrentPlayerProfile(UserId userId);
    
    /**
     * Update player profile with selective fields
     * Only updates fields that are present in the request (not null)
     */
    PlayerProfileUpdateResponse updatePlayerProfile(UserId userId, PlayerProfileUpdateRequest request);
}
