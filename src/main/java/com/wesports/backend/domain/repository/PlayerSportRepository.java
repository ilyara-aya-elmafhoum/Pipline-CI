package com.wesports.backend.domain.repository;

import com.wesports.backend.domain.model.PlayerSport;
import com.wesports.backend.domain.valueobject.UserId;
import com.wesports.backend.domain.valueobject.PlayerId;
import com.wesports.backend.domain.valueobject.SportId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Domain repository interface for PlayerSport aggregate
 * Follows hexagonal architecture - this is the port that will be implemented by infrastructure
 */
public interface PlayerSportRepository {
    
    /**
     * Save a new PlayerSport association
     */
    PlayerSport save(PlayerSport playerSport);
    
    /**
     * Find association by ID
     */
    Optional<PlayerSport> findById(UUID id);
    
    /**
     * Find all active associations for a user
     */
    List<PlayerSport> findActiveByUserId(UserId userId);
    
    /**
     * Find all active associations for a player profile
     */
    List<PlayerSport> findActiveByPlayerId(PlayerId playerId);
    
    /**
     * Find all active associations for a sport
     */
    List<PlayerSport> findActiveBySportId(SportId sportId);
    
    /**
     * Find a specific association between user, player, and sport
     */
    Optional<PlayerSport> findActiveAssociation(UserId userId, PlayerId playerId, SportId sportId);
    
    /**
     * Check if an active association exists
     */
    boolean existsActiveAssociation(UserId userId, PlayerId playerId, SportId sportId);
    
    /**
     * Find all associations (active and inactive)
     */
    List<PlayerSport> findAll();
    
    /**
     * Delete an association
     */
    void delete(PlayerSport playerSport);
    
    /**
     * Delete association by ID
     */
    void deleteById(UUID id);
}
