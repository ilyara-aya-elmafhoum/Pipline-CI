package com.wesports.backend.infrastructure.persistence.mapper;

import com.wesports.backend.domain.model.Sport;
import com.wesports.backend.domain.valueobject.SportId;
import com.wesports.backend.infrastructure.persistence.entity.SportEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Sport domain model and SportEntity
 */
@Component
public class SportMapper {

    public SportEntity toEntity(Sport sport) {
        if (sport == null) {
            return null;
        }

        return new SportEntity(
                sport.getId().getValue(),
                sport.getName(),
                sport.getCode(),
                sport.getDescription(),
                sport.isActive(),
                sport.getCreatedAt(),
                sport.getUpdatedAt()
        );
    }

    public Sport toDomain(SportEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Sport(
                SportId.of(entity.getId()),
                entity.getName(),
                entity.getCode(),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public List<Sport> toDomainList(List<SportEntity> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public List<SportEntity> toEntityList(List<Sport> sports) {
        if (sports == null) {
            return null;
        }

        return sports.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
