package com.wesports.backend.infrastructure.persistence.jpa;

import com.wesports.backend.infrastructure.persistence.entity.PlayerSportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for PlayerSportEntity
 */
@Repository
public interface SpringPlayerSportRepository extends JpaRepository<PlayerSportEntity, UUID> {
    
    /**
     * Find all active associations for a user
     */
    @Query("SELECT ps FROM PlayerSportEntity ps WHERE ps.userId = :userId AND ps.active = true")
    List<PlayerSportEntity> findActiveByUserId(@Param("userId") UUID userId);
    
    /**
     * Find all active associations for a player profile
     */
    @Query("SELECT ps FROM PlayerSportEntity ps WHERE ps.playerId = :playerId AND ps.active = true")
    List<PlayerSportEntity> findActiveByPlayerId(@Param("playerId") UUID playerId);
    
    /**
     * Find all active associations for a sport
     */
    @Query("SELECT ps FROM PlayerSportEntity ps WHERE ps.sportId = :sportId AND ps.active = true")
    List<PlayerSportEntity> findActiveBySportId(@Param("sportId") UUID sportId);
    
    /**
     * Find a specific association
     */
    @Query("SELECT ps FROM PlayerSportEntity ps WHERE ps.userId = :userId AND ps.playerId = :playerId AND ps.sportId = :sportId AND ps.active = true")
    Optional<PlayerSportEntity> findActiveAssociation(@Param("userId") UUID userId, 
                                                     @Param("playerId") UUID playerId, 
                                                     @Param("sportId") UUID sportId);
    
    /**
     * Check if association exists
     */
    @Query("SELECT COUNT(ps) > 0 FROM PlayerSportEntity ps WHERE ps.userId = :userId AND ps.playerId = :playerId AND ps.sportId = :sportId AND ps.active = true")
    boolean existsActiveAssociation(@Param("userId") UUID userId, 
                                  @Param("playerId") UUID playerId, 
                                  @Param("sportId") UUID sportId);
}
