package com.wesports.backend.application.service;

import com.wesports.backend.application.dto.PlayerProfileResponse;
import com.wesports.backend.application.dto.PlayerProfileUpdateRequest;
import com.wesports.backend.application.dto.PlayerProfileUpdateResponse;
import com.wesports.backend.application.port.inbound.PlayerProfileService;
import com.wesports.backend.domain.valueobject.Gender;
import com.wesports.backend.domain.valueobject.PreferredFoot;
import com.wesports.backend.domain.model.Player;
import com.wesports.backend.domain.model.User;
import com.wesports.backend.domain.model.UserAuthMethod;
import com.wesports.backend.domain.repository.PlayerRepository;
import com.wesports.backend.domain.repository.UserRepository;
import com.wesports.backend.domain.repository.UserAuthMethodRepository;
import com.wesports.backend.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Application service for player profile operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerProfileServiceImpl implements PlayerProfileService {
    
    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final UserAuthMethodRepository userAuthMethodRepository;
    
    @Override
    @Transactional(readOnly = true)
    public PlayerProfileResponse getCurrentPlayerProfile(UserId userId) {
        Optional<Player> playerOpt = playerRepository.findById(userId);
        
        if (playerOpt.isPresent()) {
            Player player = playerOpt.get();
            
            // Get User data separately since Player no longer inherits from User
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.warn("User not found for existing player: {}", userId.getValue());
                throw new RuntimeException("User not found for existing player");
            }
            
            User user = userOpt.get();
            
            return PlayerProfileResponse.success(
                player.getId().getValue(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail().getValue(),
                user.getGender() != null ? user.getGender().toString() : null,
                user.getBirthday(),
                user.getLanguageId(),
                getAuthProviders(userId),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUserRole() != null ? user.getUserRole().toString() : null,
                user.getNationality(),
                user.getLieuDeResidence(),
                user.getLanguages(),
                user.isEmailVerified(),
                player.getProfilePhotoUrl(),
                player.getHeight(),
                player.getWeight(),
                player.getPostId(),
                player.getPosition() != null ? player.getPosition().name() : null,
                player.getCategory() != null ? player.getCategory().name() : null,
                user.isProfileComplete(),
                player.hasPosition(),
                player.hasCategory(),
                player.isPhysicalProfileComplete()
            );
        } else {
            // Player doesn't exist - check if user exists
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.warn("User not found: {}", userId.getValue());
                throw new RuntimeException("User not found");
            }
            
            User user = userOpt.get();
            
            // Check if user is a player role but Player entity hasn't been created yet
            if (!user.isPlayer()) {
                log.warn("User is not a player: {}", userId.getValue());
                throw new RuntimeException("User is not a player");
            }
            
            // Return user data with null player fields (incomplete profile)
            return PlayerProfileResponse.success(
                user.getId().getValue(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail().getValue(),
                user.getGender() != null ? user.getGender().toString() : null,
                user.getBirthday(),
                user.getLanguageId(),
                getAuthProviders(userId),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUserRole() != null ? user.getUserRole().toString() : null,
                user.getNationality(),
                user.getLieuDeResidence(),
                user.getLanguages(),
                user.isEmailVerified(),
                null, // profilePhotoUrl
                null, // height
                null, // weight
                null, // postId
                null, // position
                null, // category
                false, // isProfileComplete
                false, // hasPosition
                false, // hasCategory
                false  // isPhysicalProfileComplete
            );
        }
    }
    
    @Override
    @Transactional
    public PlayerProfileUpdateResponse updatePlayerProfile(UserId userId, PlayerProfileUpdateRequest request) {
        try {
            // First check if Player entity exists
            Optional<Player> playerOpt = playerRepository.findById(userId);
            Player player;
            
            // Always get User entity first since Player is now independent
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.warn("User not found: {}", userId.getValue());
                return PlayerProfileUpdateResponse.error("User not found");
            }
            
            User user = userOpt.get();
            if (!user.isPlayer()) {
                log.warn("User is not a player: {}", userId.getValue());
                return PlayerProfileUpdateResponse.error("User is not a player");
            }
            
            if (playerOpt.isPresent()) {
                // User has existing Player entity
                player = playerOpt.get();
            } else {
                // Create new Player entity (independent from User)
                player = new Player(null, null);
            }
            
            // Parse optional gender field
            Gender gender = null;
            if (request.gender() != null) {
                try {
                    gender = Gender.fromString(request.gender());
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid gender value: {}", request.gender());
                    return PlayerProfileUpdateResponse.error("Invalid gender value: " + request.gender());
                }
            }
            
            // Parse optional preferred foot field
            PreferredFoot preferredFoot = null;
            if (request.preferredFoot() != null) {
                try {
                    preferredFoot = PreferredFoot.fromString(request.preferredFoot());
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid preferred foot value: {}", request.preferredFoot());
                    return PlayerProfileUpdateResponse.error("Invalid preferred foot value: " + request.preferredFoot());
                }
            }
            
            // Update User profile (since Player is independent, User data is separate)
            user.updateProfile(
                request.firstName() != null ? request.firstName() : user.getFirstName(),
                request.lastName() != null ? request.lastName() : user.getLastName(),
                gender != null ? gender : user.getGender(),
                request.birthday() != null ? request.birthday() : user.getBirthday()
            );
            
            if (request.nationality() != null) user.setNationality(request.nationality());
            if (request.lieuDeResidence() != null) user.setLieuDeResidence(request.lieuDeResidence());
            
            // Update Player-specific fields
            if (request.height() != null || request.weight() != null || request.profilePhotoUrl() != null) {
                player.updateProfile(
                    request.profilePhotoUrl(),
                    request.height(),
                    request.weight()
                );
            }
            
            if (preferredFoot != null) {
                player.updatePreferredFoot(preferredFoot);
            }
            
            // Save both entities
            userRepository.save(user);
            Player updatedPlayer = playerRepository.save(player);
            
            // Return updated profile combining User and Player data
            PlayerProfileResponse updatedProfile = PlayerProfileResponse.success(
                updatedPlayer.getId().getValue(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail().getValue(),
                user.getGender() != null ? user.getGender().toString() : null,
                user.getBirthday(),
                user.getLanguageId(),
                getAuthProviders(userId),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUserRole() != null ? user.getUserRole().toString() : null,
                user.getNationality(),
                user.getLieuDeResidence(),
                user.getLanguages(),
                user.isEmailVerified(),
                updatedPlayer.getProfilePhotoUrl(),
                updatedPlayer.getHeight(),
                updatedPlayer.getWeight(),
                updatedPlayer.getPostId(),
                updatedPlayer.getPosition() != null ? updatedPlayer.getPosition().name() : null,
                updatedPlayer.getCategory() != null ? updatedPlayer.getCategory().name() : null,
                user.isProfileComplete(),
                updatedPlayer.hasPosition(),
                updatedPlayer.hasCategory(),
                updatedPlayer.isPhysicalProfileComplete()
            );
            
            return PlayerProfileUpdateResponse.success("Profile updated successfully", updatedProfile);
            
        } catch (Exception e) {
            log.error("Error updating player profile for user: {}", userId.getValue(), e);
            return PlayerProfileUpdateResponse.error("Failed to update profile: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to get auth providers for a user using the new UserAuthMethod architecture
     */
    private List<String> getAuthProviders(UserId userId) {
        try {
            List<UserAuthMethod> authMethods = userAuthMethodRepository.findActiveByUserId(userId);
            return authMethods.stream()
                    .map(method -> method.getAuthMethodType().getDisplayName())
                    .toList();
        } catch (Exception e) {
            log.warn("Failed to load auth providers for user: {}", userId.getValue(), e);
            return null;
        }
    }
}
