package com.wesports.backend.infrastructure.persistence.repository;

import com.wesports.backend.domain.model.UserAuthMethod;
import com.wesports.backend.domain.repository.UserAuthMethodRepository;
import com.wesports.backend.domain.valueobject.AuthMethodType;
import com.wesports.backend.domain.valueobject.Email;
import com.wesports.backend.domain.valueobject.UserAuthMethodId;
import com.wesports.backend.domain.valueobject.UserId;
import com.wesports.backend.infrastructure.persistence.entity.UserAuthMethodEntity;
import com.wesports.backend.infrastructure.persistence.jpa.SpringUserAuthMethodRepository;
import com.wesports.backend.infrastructure.persistence.mapper.UserAuthMethodMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapter implementing UserAuthMethodRepository interface
 * Bridges domain repository interface with JPA infrastructure
 * Part of the infrastructure layer in hexagonal architecture
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class UserAuthMethodRepositoryAdapter implements UserAuthMethodRepository {

    private final SpringUserAuthMethodRepository springRepository;
    private final UserAuthMethodMapper mapper;

    @Override
    public UserAuthMethod save(UserAuthMethod userAuthMethod) {
        log.debug("Saving user auth method for user: {}, authType: {}", 
                 userAuthMethod.getUserId().getValue(), userAuthMethod.getAuthMethodType());
        
        UserAuthMethodEntity entity = mapper.toEntity(userAuthMethod);
        UserAuthMethodEntity savedEntity = springRepository.save(entity);
        
        log.debug("Saved user auth method with ID: {}", savedEntity.getId());
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<UserAuthMethod> findById(UserAuthMethodId userAuthMethodId) {
        log.debug("Finding auth method by ID: {}", userAuthMethodId.getValue());
        
        return springRepository.findById(userAuthMethodId.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public List<UserAuthMethod> findByUserId(UserId userId) {
        log.debug("Finding all auth methods for user: {}", userId.getValue());
        
        List<UserAuthMethodEntity> entities = springRepository.findAllByUserId(userId.getValue());
        
        log.debug("Found {} total auth methods for user: {}", entities.size(), userId.getValue());
        return entities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<UserAuthMethod> findActiveByUserId(UserId userId) {
        log.debug("Finding active auth methods for user: {}", userId.getValue());
        
        List<UserAuthMethodEntity> entities = springRepository.findActiveByUserId(userId.getValue());
        
        log.debug("Found {} active auth methods for user: {}", entities.size(), userId.getValue());
        return entities.stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<UserAuthMethod> findPrimaryByUserId(UserId userId) {
        log.debug("Finding primary auth method for user: {}", userId.getValue());
        
        return springRepository.findPrimaryByUserId(userId.getValue())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<UserAuthMethod> findByUserIdAndAuthType(UserId userId, AuthMethodType authType) {
        log.debug("Finding auth method for user: {} and authType: {}", userId.getValue(), authType);
        
        UserAuthMethodEntity.AuthMethodTypeEnum authEnum = mapToEntityEnum(authType);
        return springRepository.findByUserIdAndAuthType(userId.getValue(), authEnum)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<UserAuthMethod> findByEmailAndAuthType(Email email, AuthMethodType authType) {
        log.debug("Finding auth method for email: {} and authType: {}", email.getValue(), authType);
        
        UserAuthMethodEntity.AuthMethodTypeEnum authEnum = mapToEntityEnum(authType);
        return springRepository.findByEmailAndAuthType(email.getValue(), authEnum)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<UserAuthMethod> findByExternalIdAndAuthType(String externalId, AuthMethodType authType) {
        log.debug("Finding auth method for external ID: {} and authType: {}", externalId, authType);
        
        UserAuthMethodEntity.AuthMethodTypeEnum authEnum = mapToEntityEnum(authType);
        return springRepository.findByExternalIdAndAuthType(externalId, authEnum)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByUserIdAndAuthType(UserId userId, AuthMethodType authType) {
        log.debug("Checking if user has auth type - user: {}, authType: {}", userId.getValue(), authType);
        
        UserAuthMethodEntity.AuthMethodTypeEnum authEnum = mapToEntityEnum(authType);
        boolean exists = springRepository.existsByUserIdAndAuthType(userId.getValue(), authEnum);
        
        log.debug("User {} has auth type {}: {}", userId.getValue(), authType, exists);
        return exists;
    }

    @Override
    public boolean existsByEmailAndAuthType(Email email, AuthMethodType authType) {
        log.debug("Checking if email exists for authType - email: {}, authType: {}", email.getValue(), authType);
        
        UserAuthMethodEntity.AuthMethodTypeEnum authEnum = mapToEntityEnum(authType);
        boolean exists = springRepository.existsByEmailAndAuthType(email.getValue(), authEnum);
        
        log.debug("Email {} exists for authType {}: {}", email.getValue(), authType, exists);
        return exists;
    }

    @Override
    public void delete(UserAuthMethod userAuthMethod) {
        log.debug("Deleting auth method: {}", userAuthMethod.getId().getValue());
        
        UserAuthMethodEntity entity = mapper.toEntity(userAuthMethod);
        springRepository.delete(entity);
        
        log.debug("Auth method deleted: {}", userAuthMethod.getId().getValue());
    }

    @Override
    public void deleteByUserId(UserId userId) {
        log.debug("Deleting all auth methods for user: {}", userId.getValue());
        
        springRepository.deleteByUserId(userId.getValue());
        
        log.debug("All auth methods deleted for user: {}", userId.getValue());
    }

    private UserAuthMethodEntity.AuthMethodTypeEnum mapToEntityEnum(AuthMethodType authMethodType) {
        return switch (authMethodType) {
            case WESPORT -> UserAuthMethodEntity.AuthMethodTypeEnum.WESPORT;
            case GOOGLE -> UserAuthMethodEntity.AuthMethodTypeEnum.GOOGLE;
            case FACEBOOK -> UserAuthMethodEntity.AuthMethodTypeEnum.FACEBOOK;
            case LINKEDIN -> UserAuthMethodEntity.AuthMethodTypeEnum.LINKEDIN;
            case APPLE -> UserAuthMethodEntity.AuthMethodTypeEnum.APPLE;
        };
    }
}
