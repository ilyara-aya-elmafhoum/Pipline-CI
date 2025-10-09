package com.wesports.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "players")
public class PlayerEntity {
    
    @Id
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;
    
    @Column(name = "height")
    private Float height;
    
    @Column(name = "weight")
    private Float weight;
    
    @Column(name = "post_id", columnDefinition = "UUID")
    private UUID postId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "position")
    private PositionEnum position;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private CategoryEnum category;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "preferred_foot")
    private PreferredFootEnum preferredFoot;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "active", nullable = false)
    private boolean active;

    // Position enum for JPA entity
    public enum PositionEnum {
        GK, CB, LB, RB, LWB, RWB, CDM, CM, CAM, LM, RM, LW, RW, ST, CF, LF, RF
    }

    // Category enum for JPA entity
    public enum CategoryEnum {
        U13, U14, U15, U16, U17, U18, U19, SENIOR
    }

    // PreferredFoot enum for JPA entity
    public enum PreferredFootEnum {
        LEFT, RIGHT, BOTH
    }

    // Default constructor
    public PlayerEntity() {
    }

    // Constructor
    public PlayerEntity(UUID id, String profilePhotoUrl, Float height, Float weight, 
                       UUID postId, PositionEnum position, CategoryEnum category, PreferredFootEnum preferredFoot,
                       LocalDateTime createdAt, LocalDateTime updatedAt, boolean active) {
        this.id = id;
        this.profilePhotoUrl = profilePhotoUrl;
        this.height = height;
        this.weight = weight;
        this.postId = postId;
        this.position = position;
        this.category = category;
        this.preferredFoot = preferredFoot;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.active = active;
    }

    // Getters and Setters
    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }

    public Float getHeight() { return height; }
    public void setHeight(Float height) { this.height = height; }

    public Float getWeight() { return weight; }
    public void setWeight(Float weight) { this.weight = weight; }

    public UUID getPostId() { return postId; }
    public void setPostId(UUID postId) { this.postId = postId; }

    public PositionEnum getPosition() { return position; }
    public void setPosition(PositionEnum position) { this.position = position; }

    public CategoryEnum getCategory() { return category; }
    public void setCategory(CategoryEnum category) { this.category = category; }

    public PreferredFootEnum getPreferredFoot() { return preferredFoot; }
    public void setPreferredFoot(PreferredFootEnum preferredFoot) { this.preferredFoot = preferredFoot; }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
