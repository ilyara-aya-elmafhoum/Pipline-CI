package com.wesports.backend.infrastructure.persistence.jpa;

import com.wesports.backend.infrastructure.persistence.entity.SportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for SportEntity
 */
@Repository
public interface SpringSportRepository extends JpaRepository<SportEntity, UUID> {
    
    /**
     * Find sport by code
     */
    Optional<SportEntity> findByCode(String code);
    
    /**
     * Find all active sports
     */
    @Query("SELECT s FROM SportEntity s WHERE s.active = true ORDER BY s.name")
    List<SportEntity> findAllActive();
    
    /**
     * Find sport by name (case insensitive)
     */
    @Query("SELECT s FROM SportEntity s WHERE LOWER(s.name) = LOWER(:name)")
    Optional<SportEntity> findByNameIgnoreCase(@Param("name") String name);
    
    /**
     * Check if sport code exists
     */
    boolean existsByCode(String code);
    
    /**
     * Check if sport name exists (case insensitive)
     */
    @Query("SELECT COUNT(s) > 0 FROM SportEntity s WHERE LOWER(s.name) = LOWER(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);
}
