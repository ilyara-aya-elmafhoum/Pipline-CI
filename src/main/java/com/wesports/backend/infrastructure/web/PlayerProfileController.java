package com.wesports.backend.infrastructure.web;

import com.wesports.backend.application.dto.PlayerProfileResponse;
import com.wesports.backend.application.dto.PlayerProfileUpdateRequest;
import com.wesports.backend.application.dto.PlayerProfileUpdateResponse;
import com.wesports.backend.application.port.inbound.PlayerProfileService;
import com.wesports.backend.application.service.AuthenticationContextService;
import com.wesports.backend.domain.valueobject.UserId;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

/**
 * REST controller for player profile operations
 * Handles HTTP requests for player profile data
 * Part of the infrastructure layer in hexagonal architecture
 */
@RestController
@RequestMapping("/api/player")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class PlayerProfileController {
    
    private static final Logger logger = Logger.getLogger(PlayerProfileController.class.getName());
    
    private final PlayerProfileService playerProfileService;
    private final AuthenticationContextService authenticationContextService;
    
    /**
     * Get current authenticated player's complete profile
     * GET /api/player/me
     */
    @GetMapping("/me")
    public ResponseEntity<PlayerProfileResponse> getCurrentPlayer(HttpServletRequest httpRequest) {
        try {
            logger.info("=== GET PLAYER PROFILE /me ===");
            
            // Use exact same pattern as onboarding endpoints
            UserId userId = authenticationContextService.getAuthenticatedUserId(httpRequest);
            logger.info("Authenticated user ID: " + userId.getValue());
            
            PlayerProfileResponse response = playerProfileService.getCurrentPlayerProfile(userId);
            
            logger.info("Player profile retrieved successfully for user: " + userId.getValue());
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            logger.warning("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(null);
        } catch (Exception e) {
            logger.severe("Error retrieving player profile: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body(null);
        }
    }
    
    /**
     * Update current authenticated player's profile
     * PUT /api/player/profile
     * Only updates fields that are present in the request body.
     * Missing fields are ignored (not updated).
     */
    @PutMapping("/profile")
    public ResponseEntity<PlayerProfileUpdateResponse> updatePlayerProfile(
            @RequestBody PlayerProfileUpdateRequest request,
            HttpServletRequest httpRequest) {
        try {
            logger.info("=== UPDATE PLAYER PROFILE ===");
            
            // Get authenticated user ID using same pattern as other endpoints
            UserId userId = authenticationContextService.getAuthenticatedUserId(httpRequest);
            logger.info("Authenticated user ID: " + userId.getValue());
            
            // Call service to update profile
            PlayerProfileUpdateResponse response = playerProfileService.updatePlayerProfile(userId, request);
            
            if (response.status().equals("success")) {
                logger.info("Player profile updated successfully for user: " + userId.getValue());
                return ResponseEntity.ok(response);
            } else {
                logger.warning("Player profile update failed for user: " + userId.getValue() + " - " + response.message());
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (RuntimeException e) {
            logger.warning("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(PlayerProfileUpdateResponse.error("Authentication required"));
        } catch (Exception e) {
            logger.severe("Error updating player profile: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body(PlayerProfileUpdateResponse.error("Failed to update profile"));
        }
    }
}
