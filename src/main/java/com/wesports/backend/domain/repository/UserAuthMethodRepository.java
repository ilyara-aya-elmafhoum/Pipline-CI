package com.wesports.backend.domain.repository;

import com.wesports.backend.domain.model.UserAuthMethod;
import com.wesports.backend.domain.valueobject.AuthMethodType;
import com.wesports.backend.domain.valueobject.Email;
import com.wesports.backend.domain.valueobject.UserAuthMethodId;
import com.wesports.backend.domain.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserAuthMethod domain objects
 * Part of the domain layer in hexagonal architecture
 */
public interface UserAuthMethodRepository {
    
    /**
     * Save a user authentication method
     */
    UserAuthMethod save(UserAuthMethod userAuthMethod);
    
    /**
     * Find user auth method by ID
     */
    Optional<UserAuthMethod> findById(UserAuthMethodId id);
    
    /**
     * Find all authentication methods for a user
     */
    List<UserAuthMethod> findByUserId(UserId userId);
    
    /**
     * Find active authentication methods for a user
     */
    List<UserAuthMethod> findActiveByUserId(UserId userId);
    
    /**
     * Find primary authentication method for a user
     */
    Optional<UserAuthMethod> findPrimaryByUserId(UserId userId);
    
    /**
     * Find authentication method by user ID and auth type
     */
    Optional<UserAuthMethod> findByUserIdAndAuthType(UserId userId, AuthMethodType authType);
    
    /**
     * Find authentication method by email and auth type
     */
    Optional<UserAuthMethod> findByEmailAndAuthType(Email email, AuthMethodType authType);

    
    /**
     * Find authentication method by external ID and auth type (for OAuth)
     */
    Optional<UserAuthMethod> findByExternalIdAndAuthType(String externalId, AuthMethodType authType);
    
    /**
     * Check if user has authentication method of specific type
     */
    boolean existsByUserIdAndAuthType(UserId userId, AuthMethodType authType);
    
    /**
     * Check if email is already used for specific auth type
     */
    boolean existsByEmailAndAuthType(Email email, AuthMethodType authType);
    
    /**
     * Delete a user authentication method
     */
    void delete(UserAuthMethod userAuthMethod);
    
    /**
     * Delete all authentication methods for a user
     */
    void deleteByUserId(UserId userId);
}
