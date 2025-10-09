package com.wesports.backend.infrastructure.persistence.mapper;

import com.wesports.backend.domain.model.UserAuthMethod;
import com.wesports.backend.domain.valueobject.AuthMethodType;
import com.wesports.backend.domain.valueobject.Email;
import com.wesports.backend.domain.valueobject.UserAuthMethodId;
import com.wesports.backend.domain.valueobject.UserId;
import com.wesports.backend.infrastructure.persistence.entity.UserAuthMethodEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper between UserAuthMethod domain model and UserAuthMethodEntity JPA entity
 * Part of the infrastructure layer in hexagonal architecture
 */
@Component
public class UserAuthMethodMapper {

    public UserAuthMethodEntity toEntity(UserAuthMethod domain) {
        if (domain == null) {
            return null;
        }

        UserAuthMethodEntity entity = new UserAuthMethodEntity();
        entity.setId(domain.getId().getValue());
        entity.setUserId(domain.getUserId().getValue());
        entity.setAuthMethodType(mapAuthMethodTypeToEnum(domain.getAuthMethodType()));
        entity.setAuthMethodName(domain.getAuthMethodName());
        entity.setEmail(domain.getEmail().getValue());
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setExternalId(domain.getExternalId());
        entity.setPrimary(domain.isPrimary());
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setLastUsedAt(domain.getLastUsedAt());

        return entity;
    }

    public UserAuthMethod toDomain(UserAuthMethodEntity entity) {
        if (entity == null) {
            return null;
        }

        return new UserAuthMethod(
            UserAuthMethodId.of(entity.getId()),
            UserId.of(entity.getUserId()),
            mapEnumToAuthMethodType(entity.getAuthMethodType()),
            entity.getAuthMethodName(),
            Email.of(entity.getEmail()),
            entity.getPasswordHash(),
            entity.getExternalId(),
            entity.isPrimary(),
            entity.isActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getLastUsedAt()
        );
    }

    private UserAuthMethodEntity.AuthMethodTypeEnum mapAuthMethodTypeToEnum(AuthMethodType authMethodType) {
        if (authMethodType == null) {
            return null;
        }

        return switch (authMethodType) {
            case WESPORT -> UserAuthMethodEntity.AuthMethodTypeEnum.WESPORT;
            case GOOGLE -> UserAuthMethodEntity.AuthMethodTypeEnum.GOOGLE;
            case FACEBOOK -> UserAuthMethodEntity.AuthMethodTypeEnum.FACEBOOK;
            case LINKEDIN -> UserAuthMethodEntity.AuthMethodTypeEnum.LINKEDIN;
            case APPLE -> UserAuthMethodEntity.AuthMethodTypeEnum.APPLE;
        };
    }

    private AuthMethodType mapEnumToAuthMethodType(UserAuthMethodEntity.AuthMethodTypeEnum authMethodTypeEnum) {
        if (authMethodTypeEnum == null) {
            return null;
        }

        return switch (authMethodTypeEnum) {
            case WESPORT -> AuthMethodType.WESPORT;
            case GOOGLE -> AuthMethodType.GOOGLE;
            case FACEBOOK -> AuthMethodType.FACEBOOK;
            case LINKEDIN -> AuthMethodType.LINKEDIN;
            case APPLE -> AuthMethodType.APPLE;
        };
    }
}
