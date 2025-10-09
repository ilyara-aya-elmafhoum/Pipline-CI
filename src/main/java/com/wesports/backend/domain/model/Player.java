package com.wesports.backend.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import com.wesports.backend.domain.valueobject.PlayerId;
import com.wesports.backend.domain.valueobject.UserId;
import com.wesports.backend.domain.valueobject.Position;
import com.wesports.backend.domain.valueobject.Category;
import com.wesports.backend.domain.valueobject.PreferredFoot;

/**
 * Player domain model - independent entity representing player data
 * No longer inherits from User - uses junction table for relationships
 */
public class Player {
    private PlayerId id;
    private String profilePhotoUrl;
    private Float height;
    private Float weight;
    private UUID postId;
    private Position position;
    private Category category;
    private PreferredFoot preferredFoot;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;

    // Minimal constructor for role selection - position/category set later during onboarding
    public Player() {
        this.id = PlayerId.generate();
        this.createdAt = LocalDateTime.now();
        this.active = true;
        // position and category are null initially, set during onboarding
    }

    // Constructor for role selection with specific User ID as Player ID
    public Player(UserId userId) {
        this.id = PlayerId.of(userId.getValue());
        this.createdAt = LocalDateTime.now();
        this.active = true;
        // position and category are null initially, set during onboarding
    }

    // Constructor for creating new players with position and category
    public Player(Position position, Category category) {
        this();
        this.position = Objects.requireNonNull(position, "Position cannot be null");
        this.category = Objects.requireNonNull(category, "Category cannot be null");
    }

    // Full constructor for loading from database
    public Player(PlayerId id, String profilePhotoUrl, Float height, Float weight, 
                  UUID postId, Position position, Category category, PreferredFoot preferredFoot,
                  LocalDateTime createdAt, LocalDateTime updatedAt, boolean active) {
        this.id = Objects.requireNonNull(id, "Player ID cannot be null");
        this.profilePhotoUrl = profilePhotoUrl;
        this.height = height;
        this.weight = weight;
        this.postId = postId;
        this.position = position;
        this.category = category;
        this.preferredFoot = preferredFoot;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = updatedAt;
        this.active = active;
    }

    // Business methods
    public void updateProfile(String profilePhotoUrl, Float height, Float weight) {
        this.profilePhotoUrl = profilePhotoUrl;
        this.height = height;
        this.weight = weight;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePosition(Position position) {
        this.position = Objects.requireNonNull(position, "Position cannot be null");
        this.updatedAt = LocalDateTime.now();
    }

    public void updateCategory(Category category) {
        this.category = Objects.requireNonNull(category, "Category cannot be null");
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePreferredFoot(PreferredFoot preferredFoot) {
        this.preferredFoot = preferredFoot;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isPhysicalProfileComplete() {
        return height != null && weight != null && position != null;
    }

    public boolean hasPosition() {
        return position != null;
    }

    public boolean hasCategory() {
        return category != null;
    }

    // Getters
    public PlayerId getId() {
        return id;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public Float getHeight() {
        return height;
    }

    public Float getWeight() {
        return weight;
    }

    public UUID getPostId() {
        return postId;
    }

    public Position getPosition() {
        return position;
    }

    public Category getCategory() {
        return category;
    }

    public PreferredFoot getPreferredFoot() {
        return preferredFoot;
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
        Player player = (Player) o;
        return Objects.equals(id, player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", height=" + height +
                ", weight=" + weight +
                ", position=" + position +
                ", category=" + category +
                ", active=" + active +
                '}';
    }
}
