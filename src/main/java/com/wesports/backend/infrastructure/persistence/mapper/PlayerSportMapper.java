package com.wesports.backend.infrastructure.persistence.mapper;

import com.wesports.backend.domain.model.PlayerSport;
import com.wesports.backend.domain.valueobject.UserId;
import com.wesports.backend.domain.valueobject.PlayerId;
import com.wesports.backend.domain.valueobject.SportId;
import com.wesports.backend.infrastructure.persistence.entity.PlayerSportEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between PlayerSport domain model and PlayerSportEntity
 */
@Component
public class PlayerSportMapper {

    public PlayerSportEntity toEntity(PlayerSport playerSport) {
        if (playerSport == null) {
            return null;
        }

        return new PlayerSportEntity(
                playerSport.getId(),
                playerSport.getUserId().getValue(),
                playerSport.getPlayerId().getValue(),
                playerSport.getSportId().getValue(),
                playerSport.getCreatedAt(),
                playerSport.getUpdatedAt(),
                playerSport.isActive()
        );
    }

    public PlayerSport toDomain(PlayerSportEntity entity) {
        if (entity == null) {
            return null;
        }

        return new PlayerSport(
                entity.getId(),
                UserId.of(entity.getUserId()),
                PlayerId.of(entity.getPlayerId()),
                SportId.of(entity.getSportId()),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isActive()
        );
    }

    public List<PlayerSport> toDomainList(List<PlayerSportEntity> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public List<PlayerSportEntity> toEntityList(List<PlayerSport> playerSports) {
        if (playerSports == null) {
            return null;
        }

        return playerSports.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
