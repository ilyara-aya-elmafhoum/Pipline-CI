package com.wesports.backend.domain.repository;

import com.wesports.backend.domain.model.Sport;
import com.wesports.backend.domain.valueobject.SportId;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for Sport aggregate
 * Follows hexagonal architecture - this is the port that will be implemented by infrastructure
 */
public interface SportRepository {
    
    /**
     * Save a new Sport
     */
    Sport save(Sport sport);
    
    /**
     * Find sport by ID
     */
    Optional<Sport> findById(SportId sportId);
    
    /**
     * Find sport by code
     */
    Optional<Sport> findByCode(String code);
    
    /**
     * Find sport by name (case insensitive)
     */
    Optional<Sport> findByName(String name);
    
    /**
     * Find all active sports
     */
    List<Sport> findAllActive();
    
    /**
     * Find all sports (active and inactive)
     */
    List<Sport> findAll();
    
    /**
     * Check if sport code exists
     */
    boolean existsByCode(String code);
    
    /**
     * Check if sport name exists (case insensitive)
     */
    boolean existsByName(String name);
    
    /**
     * Delete a sport
     */
    void delete(Sport sport);
    
    /**
     * Delete sport by ID
     */
    void deleteById(SportId sportId);
}
