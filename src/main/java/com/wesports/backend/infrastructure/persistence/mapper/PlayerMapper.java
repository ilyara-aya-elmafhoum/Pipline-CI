package com.wesports.backend.infrastructure.persistence.mapper;

import com.wesports.backend.domain.model.Player;
import com.wesports.backend.domain.valueobject.PlayerId;
import com.wesports.backend.domain.valueobject.Position;
import com.wesports.backend.domain.valueobject.Category;
import com.wesports.backend.domain.valueobject.PreferredFoot;
import com.wesports.backend.infrastructure.persistence.entity.PlayerEntity;
import org.springframework.stereotype.Component;




@Component
public class PlayerMapper {

    public PlayerEntity toEntity(Player player) {
        if (player == null) {
            return null;
        }

        return new PlayerEntity(
                player.getId().getValue(),
                player.getProfilePhotoUrl(),
                player.getHeight(),
                player.getWeight(),
                player.getPostId(),
                mapPositionToEnum(player.getPosition()),
                mapCategoryToEnum(player.getCategory()),
                mapPreferredFootToEnum(player.getPreferredFoot()),
                player.getCreatedAt(),
                player.getUpdatedAt(),
                player.isActive()
        );
    }

    public Player toDomain(PlayerEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return new Player(
            PlayerId.of(entity.getId()),
            entity.getProfilePhotoUrl(),
            entity.getHeight(),
            entity.getWeight(),
            entity.getPostId(),
            mapEnumToPosition(entity.getPosition()),
            mapEnumToCategory(entity.getCategory()),
            mapEnumToPreferredFoot(entity.getPreferredFoot()),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.isActive()
        );
    }

    private PlayerEntity.PositionEnum mapPositionToEnum(Position position) {
        if (position == null) {
            return null;
        }
        
        try {
            return PlayerEntity.PositionEnum.valueOf(position.name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown position: " + position);
        }
    }

    private Position mapEnumToPosition(PlayerEntity.PositionEnum positionEnum) {
        if (positionEnum == null) {
            return null;
        }
        
        try {
            return Position.valueOf(positionEnum.name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown position enum: " + positionEnum);
        }
    }

    private PlayerEntity.CategoryEnum mapCategoryToEnum(Category category) {
        if (category == null) {
            return null;
        }
        
        try {
            return PlayerEntity.CategoryEnum.valueOf(category.name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown category: " + category);
        }
    }

    private Category mapEnumToCategory(PlayerEntity.CategoryEnum categoryEnum) {
        if (categoryEnum == null) {
            return null;
        }
        
        try {
            return Category.valueOf(categoryEnum.name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown category enum: " + categoryEnum);
        }
    }

    private PlayerEntity.PreferredFootEnum mapPreferredFootToEnum(PreferredFoot foot) {
        if (foot == null) {
            return null;
        }
        
        try {
            return PlayerEntity.PreferredFootEnum.valueOf(foot.name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown preferred foot: " + foot);
        }
    }

    private PreferredFoot mapEnumToPreferredFoot(PlayerEntity.PreferredFootEnum footEnum) {
        if (footEnum == null) {
            return null;
        }
        
        try {
            return PreferredFoot.valueOf(footEnum.name());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown preferred foot enum: " + footEnum);
        }
    }
}
