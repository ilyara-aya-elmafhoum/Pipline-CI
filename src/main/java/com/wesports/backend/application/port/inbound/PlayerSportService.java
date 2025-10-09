package com.wesports.backend.application.port.inbound;

import com.wesports.backend.domain.valueobject.UserId;
import com.wesports.backend.domain.valueobject.PlayerId;
import com.wesports.backend.domain.valueobject.SportId;
import com.wesports.backend.domain.model.PlayerSport;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Inbound port for PlayerSport service operations
 * Part of the application layer in hexagonal architecture
 */
public interface PlayerSportService {
    
    /**
     * Create a new association between user, player profile, and sport
     */
    PlayerSport createAssociation(UserId userId, PlayerId playerId, SportId sportId);
    
    /**
     * Find all active associations for a user
     */
    List<PlayerSport> findUserAssociations(UserId userId);
    
    /**
     * Find all active associations for a player profile
     */
    List<PlayerSport> findPlayerAssociations(PlayerId playerId);
    
    /**
     * Find all active associations for a sport
     */
    List<PlayerSport> findSportAssociations(SportId sportId);
    
    /**
     * Find a specific association
     */
    Optional<PlayerSport> findAssociation(UserId userId, PlayerId playerId, SportId sportId);
    
    /**
     * Check if an association exists
     */
    boolean hasAssociation(UserId userId, PlayerId playerId, SportId sportId);
    
    /**
     * Deactivate an association (soft delete)
     */
    void deactivateAssociation(UUID associationId);
    
    /**
     * Activate an association
     */
    void activateAssociation(UUID associationId);
    
    /**
     * Delete an association permanently
     */
    void deleteAssociation(UUID associationId);
    
    /**
     * Get all associations for analytics
     */
    List<PlayerSport> getAllAssociations();
}
