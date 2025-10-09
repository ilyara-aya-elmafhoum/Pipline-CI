package com.wesports.backend.infrastructure.persistence.jpa;

import com.wesports.backend.infrastructure.persistence.entity.UserAuthMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for UserAuthMethodEntity
 * Part of the infrastructure layer in hexagonal architecture
 */
@Repository
public interface SpringUserAuthMethodRepository extends JpaRepository<UserAuthMethodEntity, UUID> {

    /**
     * Find all active authentication methods for a user
     */
    @Query("SELECT uam FROM UserAuthMethodEntity uam WHERE uam.userId = :userId AND uam.isActive = true ORDER BY uam.createdAt")
    List<UserAuthMethodEntity> findActiveByUserId(@Param("userId") UUID userId);

    /**
     * Find all authentication methods for a user (including inactive)
     */
    @Query("SELECT uam FROM UserAuthMethodEntity uam WHERE uam.userId = :userId ORDER BY uam.createdAt")
    List<UserAuthMethodEntity> findAllByUserId(@Param("userId") UUID userId);

    /**
     * Find primary authentication method for a user
     */
    @Query("SELECT uam FROM UserAuthMethodEntity uam WHERE uam.userId = :userId AND uam.isPrimary = true AND uam.isActive = true")
    Optional<UserAuthMethodEntity> findPrimaryByUserId(@Param("userId") UUID userId);

    /**
     * Find authentication method by user ID and auth method type
     */
    @Query("SELECT uam FROM UserAuthMethodEntity uam WHERE uam.userId = :userId AND uam.authMethodType = :authMethodType AND uam.isActive = true")
    Optional<UserAuthMethodEntity> findByUserIdAndAuthType(@Param("userId") UUID userId, 
                                                          @Param("authMethodType") UserAuthMethodEntity.AuthMethodTypeEnum authMethodType);

    /**
     * Find authentication method by email and auth method type
     */
    @Query("SELECT uam FROM UserAuthMethodEntity uam WHERE uam.email = :email AND uam.authMethodType = :authMethodType AND uam.isActive = true")
    Optional<UserAuthMethodEntity> findByEmailAndAuthType(@Param("email") String email, 
                                                         @Param("authMethodType") UserAuthMethodEntity.AuthMethodTypeEnum authMethodType);

    /**
     * Find authentication method by external ID and auth method type (for OAuth)
     */
    @Query("SELECT uam FROM UserAuthMethodEntity uam WHERE uam.externalId = :externalId AND uam.authMethodType = :authMethodType AND uam.isActive = true")
    Optional<UserAuthMethodEntity> findByExternalIdAndAuthType(@Param("externalId") String externalId,
                                                              @Param("authMethodType") UserAuthMethodEntity.AuthMethodTypeEnum authMethodType);

    /**
     * Check if user has authentication method of specific type
     */
    @Query("SELECT COUNT(uam) > 0 FROM UserAuthMethodEntity uam WHERE uam.userId = :userId AND uam.authMethodType = :authMethodType AND uam.isActive = true")
    boolean existsByUserIdAndAuthType(@Param("userId") UUID userId, 
                                     @Param("authMethodType") UserAuthMethodEntity.AuthMethodTypeEnum authMethodType);

    /**
     * Check if email is already used for specific auth method type
     */
    @Query("SELECT COUNT(uam) > 0 FROM UserAuthMethodEntity uam WHERE uam.email = :email AND uam.authMethodType = :authMethodType AND uam.isActive = true")
    boolean existsByEmailAndAuthType(@Param("email") String email, 
                                    @Param("authMethodType") UserAuthMethodEntity.AuthMethodTypeEnum authMethodType);

    /**
     * Delete all authentication methods for a user
     */
    @Modifying
    @Query("DELETE FROM UserAuthMethodEntity uam WHERE uam.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);

    /**
     * Find by email for any active auth method (useful for login)
     */
    @Query("SELECT uam FROM UserAuthMethodEntity uam WHERE uam.email = :email AND uam.isActive = true ORDER BY uam.authMethodType")
    List<UserAuthMethodEntity> findByEmail(@Param("email") String email);
}
