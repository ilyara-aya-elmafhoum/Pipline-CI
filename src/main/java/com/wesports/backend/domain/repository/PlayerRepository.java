package com.wesports.backend.domain.repository;

import com.wesports.backend.domain.model.Player;
import com.wesports.backend.domain.valueobject.UserId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository {
    
    Player save(Player player);
    
    Player merge(Player player);
    
    Optional<Player> findById(UserId playerId);
    
    List<Player> findByPostId(UUID postId);
    
    List<Player> findAll();
    
    void delete(Player player);
    
    void deleteById(UserId playerId);
}
