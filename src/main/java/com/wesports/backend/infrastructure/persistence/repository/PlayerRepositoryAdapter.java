package com.wesports.backend.infrastructure.persistence.repository;

import com.wesports.backend.domain.model.Player;
import com.wesports.backend.domain.repository.PlayerRepository;
import com.wesports.backend.domain.valueobject.UserId;
import com.wesports.backend.infrastructure.persistence.entity.PlayerEntity;
import com.wesports.backend.infrastructure.persistence.jpa.SpringPlayerRepository;
import com.wesports.backend.infrastructure.persistence.mapper.PlayerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of PlayerRepository that adapts JPA repository to domain repository
 * Part of the infrastructure layer in hexagonal architecture
 */
@Repository
public class PlayerRepositoryAdapter implements PlayerRepository {

    private final SpringPlayerRepository springPlayerRepository;
    private final PlayerMapper playerMapper;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public PlayerRepositoryAdapter(SpringPlayerRepository springPlayerRepository, PlayerMapper playerMapper) {
        this.springPlayerRepository = springPlayerRepository;
        this.playerMapper = playerMapper;
    }

    @Override
    public Player save(Player player) {
        PlayerEntity entity = playerMapper.toEntity(player);
        PlayerEntity savedEntity = springPlayerRepository.save(entity);
        return playerMapper.toDomain(savedEntity);
    }

    public Player merge(Player player) {
        try {
            // Check if this is a new player (no existing PlayerEntity)
            Optional<PlayerEntity> existingPlayer = springPlayerRepository.findById(player.getId().getValue());
            
            if (existingPlayer.isPresent()) {
                // Update existing player using normal merge
                PlayerEntity entity = playerMapper.toEntity(player);
                PlayerEntity mergedEntity = entityManager.merge(entity);
                entityManager.flush();
                return playerMapper.toDomain(mergedEntity);
            } else {
                // Create new player - use native SQL to avoid user table conflicts
                // The user already exists, we only need to create the player record
                UUID playerId = player.getId().getValue();
                String position = player.getPosition() != null ? player.getPosition().name() : null;
                String category = player.getCategory() != null ? player.getCategory().name() : null;

                // TODO : fix this
                entityManager.createNativeQuery(
                    "INSERT INTO players (id, position, category, height, weight, profile_photo_url, post_id) " +
                    "VALUES (?1, ?2, ?3, ?4, ?5, ?6, ?7)"
                )
                .setParameter(1, playerId)
                .setParameter(2, position)
                .setParameter(3, category)
                .setParameter(4, player.getHeight())
                .setParameter(5, player.getWeight())
                .setParameter(6, player.getProfilePhotoUrl())
                .setParameter(7, player.getPostId())
                .executeUpdate();
                
                entityManager.flush();
                
                // Now fetch the created player
                Optional<PlayerEntity> createdPlayer = springPlayerRepository.findById(playerId);
                return createdPlayer.map(playerMapper::toDomain).orElse(player);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to merge player: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Player> findById(UserId playerId) {
        Optional<PlayerEntity> entityOpt = springPlayerRepository.findById(playerId.getValue());
        return entityOpt.map(playerMapper::toDomain);
    }



    @Override
    public List<Player> findByPostId(UUID postId) {
        List<PlayerEntity> entities = springPlayerRepository.findByPostId(postId);
        return entities.stream()
                .map(playerMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Player> findAll() {
        List<PlayerEntity> entities = springPlayerRepository.findAll();
        return entities.stream()
                .map(playerMapper::toDomain)
                .collect(Collectors.toList());
    }



    @Override
    public void delete(Player player) {
        PlayerEntity entity = playerMapper.toEntity(player);
        springPlayerRepository.delete(entity);
    }

    @Override
    public void deleteById(UserId playerId) {
        springPlayerRepository.deleteById(playerId.getValue());
    }
}
