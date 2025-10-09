package com.wesports.backend.domain.model;

import com.wesports.backend.domain.valueobject.UserId;
import com.wesports.backend.domain.valueobject.PlayerId;
import com.wesports.backend.domain.valueobject.SportId;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * PlayerSport domain model - represents the relationship between users, players, and sports
 */
public class PlayerSport {
    private UUID id;
    private UserId userId;
    private PlayerId playerId;
    private SportId sportId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;

    // Protected constructor for framework
    protected PlayerSport() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    // Constructor for creating new associations
    public PlayerSport(UserId userId, PlayerId playerId, SportId sportId) {
        this();
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.playerId = Objects.requireNonNull(playerId, "Player ID cannot be null");
        this.sportId = Objects.requireNonNull(sportId, "Sport ID cannot be null");
    }

    // Constructor for loading from database
    public PlayerSport(UUID id, UserId userId, PlayerId playerId, SportId sportId, 
                      LocalDateTime createdAt, LocalDateTime updatedAt, boolean active) {
        this.id = Objects.requireNonNull(id, "ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.playerId = Objects.requireNonNull(playerId, "Player ID cannot be null");
        this.sportId = Objects.requireNonNull(sportId, "Sport ID cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
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

    public boolean isAssociationOf(UserId userId, SportId sportId) {
        return this.userId.equals(userId) && this.sportId.equals(sportId) && this.active;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public PlayerId getPlayerId() {
        return playerId;
    }

    public SportId getSportId() {
        return sportId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerSport that = (PlayerSport) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PlayerSport{" +
                "id=" + id +
                ", userId=" + userId +
                ", playerId=" + playerId +
                ", sportId=" + sportId +
                ", active=" + active +
                '}';
    }
}
