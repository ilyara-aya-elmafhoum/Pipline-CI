package com.wesports.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "posts")
public class PostEntity {
    
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;
    
    @Column(name = "code", nullable = false, unique = true)
    private String code;
    
    @Column(name = "label", nullable = false)
    private String label;

    public PostEntity() {}

    // Constructor
    public PostEntity(UUID id, String code, String label) {
        this.id = id;
        this.code = code;
        this.label = label;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}
