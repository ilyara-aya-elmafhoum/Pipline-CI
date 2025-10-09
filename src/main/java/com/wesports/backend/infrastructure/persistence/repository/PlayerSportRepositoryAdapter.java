package com.wesports.backend.infrastructure.persistence.repository;

import com.wesports.backend.domain.model.PlayerSport;
import com.wesports.backend.domain.repository.PlayerSportRepository;
import com.wesports.backend.domain.valueobject.UserId;
import com.wesports.backend.domain.valueobject.PlayerId;
import com.wesports.backend.domain.valueobject.SportId;
import com.wesports.backend.infrastructure.persistence.entity.PlayerSportEntity;
import com.wesports.backend.infrastructure.persistence.jpa.SpringPlayerSportRepository;
import com.wesports.backend.infrastructure.persistence.mapper.PlayerSportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of PlayerSportRepository that adapts JPA repository to domain repository
 * Part of the infrastructure layer in hexagonal architecture
 */
@Repository
public class PlayerSportRepositoryAdapter implements PlayerSportRepository {
    
    private final SpringPlayerSportRepository springPlayerSportRepository;
    private final PlayerSportMapper playerSportMapper;
    
    @Autowired
    public PlayerSportRepositoryAdapter(SpringPlayerSportRepository springPlayerSportRepository, 
                                       PlayerSportMapper playerSportMapper) {
        this.springPlayerSportRepository = springPlayerSportRepository;
        this.playerSportMapper = playerSportMapper;
    }
    
    @Override
    public PlayerSport save(PlayerSport playerSport) {
        PlayerSportEntity entity = playerSportMapper.toEntity(playerSport);
        PlayerSportEntity savedEntity = springPlayerSportRepository.save(entity);
        return playerSportMapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<PlayerSport> findById(UUID id) {
        Optional<PlayerSportEntity> entityOpt = springPlayerSportRepository.findById(id);
        return entityOpt.map(playerSportMapper::toDomain);
    }
    
    @Override
    public List<PlayerSport> findActiveByUserId(UserId userId) {
        List<PlayerSportEntity> entities = springPlayerSportRepository.findActiveByUserId(userId.getValue());
        return entities.stream()
                .map(playerSportMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PlayerSport> findActiveByPlayerId(PlayerId playerId) {
        List<PlayerSportEntity> entities = springPlayerSportRepository.findActiveByPlayerId(playerId.getValue());
        return entities.stream()
                .map(playerSportMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PlayerSport> findActiveBySportId(SportId sportId) {
        List<PlayerSportEntity> entities = springPlayerSportRepository.findActiveBySportId(sportId.getValue());
        return entities.stream()
                .map(playerSportMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<PlayerSport> findActiveAssociation(UserId userId, PlayerId playerId, SportId sportId) {
        Optional<PlayerSportEntity> entityOpt = springPlayerSportRepository.findActiveAssociation(
            userId.getValue(), playerId.getValue(), sportId.getValue());
        return entityOpt.map(playerSportMapper::toDomain);
    }
    
    @Override
    public boolean existsActiveAssociation(UserId userId, PlayerId playerId, SportId sportId) {
        return springPlayerSportRepository.existsActiveAssociation(
            userId.getValue(), playerId.getValue(), sportId.getValue());
    }
    
    @Override
    public List<PlayerSport> findAll() {
        List<PlayerSportEntity> entities = springPlayerSportRepository.findAll();
        return entities.stream()
                .map(playerSportMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public void delete(PlayerSport playerSport) {
        PlayerSportEntity entity = playerSportMapper.toEntity(playerSport);
        springPlayerSportRepository.delete(entity);
    }
    
    @Override
    public void deleteById(UUID id) {
        springPlayerSportRepository.deleteById(id);
    }
}
