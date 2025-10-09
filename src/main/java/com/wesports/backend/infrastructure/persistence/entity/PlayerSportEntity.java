package com.wesports.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * JPA Entity for PlayerSport junction table
 * Manages many-to-many relationship between Users, Players, and Sports
 */
@Entity
@Table(name = "playersport")
public class PlayerSportEntity {
    
    @Id
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "player_id", nullable = false)
    private UUID playerId;
    
    @Column(name = "sport_id", nullable = false)
    private UUID sportId;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "active", nullable = false)
    private boolean active;

    // Default constructor for JPA
    public PlayerSportEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    // Constructor
    public PlayerSportEntity(UUID userId, UUID playerId, UUID sportId) {
        this();
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.playerId = Objects.requireNonNull(playerId, "Player ID cannot be null");
        this.sportId = Objects.requireNonNull(sportId, "Sport ID cannot be null");
    }

    // Full constructor
    public PlayerSportEntity(UUID id, UUID userId, UUID playerId, UUID sportId, 
                           LocalDateTime createdAt, LocalDateTime updatedAt, boolean active) {
        this.id = id;
        this.userId = userId;
        this.playerId = playerId;
        this.sportId = sportId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.active = active;
    }

    // Business methods
    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public UUID getSportId() {
        return sportId;
    }

    public void setSportId(UUID sportId) {
        this.sportId = sportId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerSportEntity that = (PlayerSportEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PlayerSportEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", playerId=" + playerId +
                ", sportId=" + sportId +
                ", active=" + active +
                '}';
    }
}
