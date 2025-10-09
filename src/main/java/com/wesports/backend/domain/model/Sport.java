package com.wesports.backend.domain.model;

import com.wesports.backend.domain.valueobject.SportId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Sport domain model - represents different sports in the system
 */
public class Sport {
    private SportId id;
    private String name;
    private String code;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Protected constructor for framework
    protected Sport() {
        this.createdAt = LocalDateTime.now();
        this.active = true;
    }

    // Constructor for creating new sports
    public Sport(String name, String code, String description) {
        this();
        this.id = SportId.generate();
        this.name = validateName(name);
        this.code = validateCode(code);
        this.description = description;
    }

    // Constructor for loading from database
    public Sport(SportId id, String name, String code, String description, 
                boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "Sport ID cannot be null");
        this.name = validateName(name);
        this.code = validateCode(code);
        this.description = description;
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.updatedAt = updatedAt;
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

    public void updateInfo(String name, String description) {
        this.name = validateName(name);
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    // Validation methods
    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Sport name cannot be null or empty");
        }
        return name.trim();
    }

    private String validateCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Sport code cannot be null or empty");
        }
        return code.trim().toUpperCase();
    }

    // Getters
    public SportId getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sport sport = (Sport) o;
        return Objects.equals(id, sport.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Sport{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", active=" + active +
                '}';
    }
}
