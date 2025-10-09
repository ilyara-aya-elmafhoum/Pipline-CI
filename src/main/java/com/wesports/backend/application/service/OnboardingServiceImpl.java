package com.wesports.backend.application.service;

import com.wesports.backend.application.dto.OnboardingStepResponse;
import com.wesports.backend.application.dto.GenderSelectionRequest;
import com.wesports.backend.application.dto.PositionSelectionRequest;
import com.wesports.backend.application.dto.CategorySelectionRequest;
import com.wesports.backend.application.dto.PlayerProfileRequest;
import com.wesports.backend.application.port.inbound.OnboardingService;
import com.wesports.backend.domain.model.User;
import com.wesports.backend.domain.model.Player;
import com.wesports.backend.domain.repository.UserRepository;
import com.wesports.backend.domain.repository.PlayerRepository;
import com.wesports.backend.domain.valueobject.UserId;

import com.wesports.backend.domain.valueobject.Gender;
import com.wesports.backend.domain.valueobject.Position;
import com.wesports.backend.domain.valueobject.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Onboarding service implementation
 * Note: Logic will be refactored to use domain services in future iterations
 */
@Service
public class OnboardingServiceImpl implements OnboardingService {
    
    private static final Logger log = Logger.getLogger(OnboardingServiceImpl.class.getName());
    
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    
    @Autowired
    public OnboardingServiceImpl(
            UserRepository userRepository,
            PlayerRepository playerRepository) {
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
    }
    
    @Override
    @Transactional
    public OnboardingStepResponse selectGender(UserId userId, GenderSelectionRequest request) {
        try {
            // Find the user
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return OnboardingStepResponse.error("User not found");
            }
            
            User user = userOpt.get();
            
            // Convert string to Gender enum
            Gender gender = Gender.fromString(request.gender());
            if (gender == null) {
                return OnboardingStepResponse.error("Invalid gender selection");
            }
            
            // Update user's gender directly since Player no longer inherits from User
            user.updateProfile(user.getFirstName(), user.getLastName(), gender, user.getBirthday());
            user.setUpdatedAt(LocalDateTime.now());
            
            // Save the updated user
            userRepository.save(user);
            
            return OnboardingStepResponse.success("Gender selection completed successfully", "position");
            
        } catch (Exception e) {
            return OnboardingStepResponse.error("An error occurred while processing gender selection");
        }
    }
    
    @Override
    @Transactional
    public OnboardingStepResponse selectPosition(UserId userId, PositionSelectionRequest request) {
        try {
            // Find user first to validate existence
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.warning("User not found: " + userId.getValue());
                return OnboardingStepResponse.error("User not found");
            }
            
            User user = userOpt.get();
            
            // Check if user has player role
            if (!user.isPlayer()) {
                log.warning("User is not a player: " + userId.getValue());
                return OnboardingStepResponse.error("For now we handle only player");
            }
            
            // Convert string to Position enum
            Position position = Position.fromString(request.positionCode());
            if (position == null) {
                return OnboardingStepResponse.error("Invalid position selection");
            }
            
            // Find Player profile directly by UserId (Player uses UserId as its ID in this architecture)
            Optional<Player> playerOpt = playerRepository.findById(userId);
            if (playerOpt.isEmpty()) {
                log.warning("Player entity not found for userId: " + userId.getValue());
                return OnboardingStepResponse.error("Player profile not found. Please contact support.");
            }
            
            // Update existing player position
            Player player = playerOpt.get();
            player.updatePosition(position);
            playerRepository.save(player);
            
            return OnboardingStepResponse.success(
                "Position selected successfully. Onboarding complete!", 
                null // Position is the final step
            );
            
        } catch (Exception e) {
            log.severe("Position selection failed: " + e.getMessage());
            return OnboardingStepResponse.error("Failed to update position. Please try again.");
        }
    }
    
    @Override
    @Transactional
    public OnboardingStepResponse selectCategories(UserId userId, CategorySelectionRequest request) {
        try {
            // Find user first to validate existence
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.warning("User not found: " + userId.getValue());
                return OnboardingStepResponse.error("User not found");
            }
            
            User user = userOpt.get();
            
            // Check if user has player role
            if (!user.isPlayer()) {
                log.warning("User is not a player: " + userId.getValue());
                return OnboardingStepResponse.error("For now we handle only player");
            }
            
            // Find or create Player profile (Player is now independent from User)
            Optional<Player> playerOpt = playerRepository.findById(userId);
            Player player;
            
            if (playerOpt.isPresent()) {
                // Update existing player category
                player = playerOpt.get();
                if (!request.categoryCodes().isEmpty()) {
                    String categoryCode = request.categoryCodes().get(0); // Take first category
                    Category category = Category.fromString(categoryCode);
                    player.updateCategory(category);
                    playerRepository.save(player);
                }
            } else {
                // This should not happen if role selection creates Player immediately
                return OnboardingStepResponse.error("Player profile not found. Please contact support.");
            }
            
            return OnboardingStepResponse.success(
                "Categories selected successfully", 
                "select-position" // Next step is position selection
            );
            
        } catch (Exception e) {
            log.severe("Category selection failed: " + e.getMessage());
            return OnboardingStepResponse.error("Failed to update categories. Please try again.");
        }
    }
    
    @Override
    @Transactional
    public OnboardingStepResponse completePlayerProfile(UserId userId, PlayerProfileRequest request) {
        try {
            // First update the User entity (since Player is now independent)
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.warning("User not found: " + userId.getValue());
                return OnboardingStepResponse.error("User not found");
            }
            
            User user = userOpt.get();
            user.updateProfile(
                request.firstName(), 
                request.lastName(), 
                user.getGender(), 
                request.birthday()
            );
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            
            // Check if this user also has a Player profile for physical attributes
            Optional<Player> playerOpt = playerRepository.findById(userId);
            if (playerOpt.isPresent()) {
                // User has a player profile - update physical attributes
                Player player = playerOpt.get();
                
                // Update player-specific fields like height, weight, photo
                player.updateProfile(request.profilePhotoUrl(), request.height(), request.weight());
                playerRepository.save(player);
                
                log.info("Player physical profile updated successfully: " + userId.getValue());
            } else {
                log.info("User has no player profile, physical attributes ignored");
            }
            
            log.info("Profile completed successfully for user: " + userId.getValue());

            
            return OnboardingStepResponse.completed("Profile completed successfully! Welcome to Ilyara.");
            
        } catch (Exception e) {
            log.severe("Profile completion failed: " + e.getMessage());
            return OnboardingStepResponse.error("Failed to complete profile. Please try again.");
        }
    }
    
    @Override
    public OnboardingStepResponse getOnboardingStatus(UserId userId) {
        try {

            log.info("UserId: " + userId.getValue());
            
            // First check if User exists
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.warning("User not found: " + userId.getValue());
                return OnboardingStepResponse.error("User not found");
            }
            
            User user = userOpt.get();
            
            // Check User-level onboarding progress first
            if (user.getGender() == null) {
                return OnboardingStepResponse.success("Please select your gender", "select-gender");
            }
            
            // Check if profile is complete (User-level data)
            if (user.getFirstName().equals("User") || user.getLastName().equals("Name")) {
                return OnboardingStepResponse.success("Please complete your profile", "complete-profile");
            }
            
            // Now check Player-specific onboarding (if user is a player)
            if (user.isPlayer()) {
                Optional<Player> playerOpt = playerRepository.findById(userId);
                if (playerOpt.isPresent()) {
                    Player player = playerOpt.get();
                    
                    // Check if player has position and category
                    if (!player.hasPosition()) {
                        return OnboardingStepResponse.success("Please select your position", "select-position");
                    }
                    
                    // Check if physical profile is complete
                    if (!player.isPhysicalProfileComplete()) {
                        return OnboardingStepResponse.success("Please complete your player profile", "complete-profile");
                    }
                    
                    log.info("Onboarding already completed for player: " + userId.getValue());
                    return OnboardingStepResponse.completed("Onboarding already completed");
                } else {
                    // User is a player but no Player profile exists - needs position selection
                    return OnboardingStepResponse.success("Please select your position", "select-position");
                }
            } else {
                // User is not a player - onboarding complete for regular users
                log.info("Onboarding completed for regular user: " + userId.getValue());
                return OnboardingStepResponse.completed("Onboarding already completed");
            }
            
        } catch (Exception e) {
            log.severe("Get onboarding status failed: " + e.getMessage());
            return OnboardingStepResponse.error("Failed to get onboarding status. Please try again.");
        }
    }
}
