package com.wesports.backend.application.service;

import com.wesports.backend.domain.model.User;
import com.wesports.backend.domain.repository.UserRepository;
import com.wesports.backend.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Application service for user queries
 * Orchestrates user retrieval operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueryService {
    
    private final UserRepository userRepository;
    
    /**
     * Find user by ID
     */
    public Optional<User> findById(UserId userId) {
        log.debug("Finding user by ID: {}", userId.getValue());
        return userRepository.findById(userId);
    }
    
    /**
     * Get user by ID or throw exception
     */
    public User getById(UserId userId) {
        log.debug("Getting user by ID: {}", userId.getValue());
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found for ID: {}", userId.getValue());
                    return new RuntimeException("User not found");
                });
    }
    
    /**
     * Check if user exists by ID
     */
    public boolean existsById(UserId userId) {
        log.debug("Checking if user exists by ID: {}", userId.getValue());
        return userRepository.findById(userId).isPresent();
    }
}
