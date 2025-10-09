package com.wesports.backend.application.service;

import com.wesports.backend.application.port.inbound.PlayerSportService;
import com.wesports.backend.domain.model.PlayerSport;
import com.wesports.backend.domain.repository.PlayerSportRepository;
import com.wesports.backend.domain.valueobject.UserId;
import com.wesports.backend.domain.valueobject.PlayerId;
import com.wesports.backend.domain.valueobject.SportId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for PlayerSport operations
 * Part of the application layer in hexagonal architecture
 */
@Service
@Transactional
public class PlayerSportServiceImpl implements PlayerSportService {
    
    private final PlayerSportRepository playerSportRepository;
    
    @Autowired
    public PlayerSportServiceImpl(PlayerSportRepository playerSportRepository) {
        this.playerSportRepository = playerSportRepository;
    }
    
    @Override
    public PlayerSport createAssociation(UserId userId, PlayerId playerId, SportId sportId) {
        // Check if association already exists
        if (playerSportRepository.existsActiveAssociation(userId, playerId, sportId)) {
            throw new IllegalStateException("Association already exists between user, player, and sport");
        }
        
        // Create new association
        PlayerSport playerSport = new PlayerSport(userId, playerId, sportId);
        return playerSportRepository.save(playerSport);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PlayerSport> findUserAssociations(UserId userId) {
        return playerSportRepository.findActiveByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PlayerSport> findPlayerAssociations(PlayerId playerId) {
        return playerSportRepository.findActiveByPlayerId(playerId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PlayerSport> findSportAssociations(SportId sportId) {
        return playerSportRepository.findActiveBySportId(sportId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<PlayerSport> findAssociation(UserId userId, PlayerId playerId, SportId sportId) {
        return playerSportRepository.findActiveAssociation(userId, playerId, sportId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasAssociation(UserId userId, PlayerId playerId, SportId sportId) {
        return playerSportRepository.existsActiveAssociation(userId, playerId, sportId);
    }
    
    @Override
    public void deactivateAssociation(UUID associationId) {
        Optional<PlayerSport> playerSportOpt = playerSportRepository.findById(associationId);
        if (playerSportOpt.isPresent()) {
            PlayerSport playerSport = playerSportOpt.get();
            playerSport.deactivate();
            playerSportRepository.save(playerSport);
        } else {
            throw new IllegalArgumentException("Association not found with ID: " + associationId);
        }
    }
    
    @Override
    public void activateAssociation(UUID associationId) {
        Optional<PlayerSport> playerSportOpt = playerSportRepository.findById(associationId);
        if (playerSportOpt.isPresent()) {
            PlayerSport playerSport = playerSportOpt.get();
            playerSport.activate();
            playerSportRepository.save(playerSport);
        } else {
            throw new IllegalArgumentException("Association not found with ID: " + associationId);
        }
    }
    
    @Override
    public void deleteAssociation(UUID associationId) {
        playerSportRepository.deleteById(associationId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PlayerSport> getAllAssociations() {
        return playerSportRepository.findAll();
    }
}
