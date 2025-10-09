package com.wesports.backend.domain.model;

import java.util.Objects;
import java.util.UUID;

public class Post {
    private UUID id;
    private String code;
    private String label;

    // Constructor for creating new posts
    public Post(String code, String label) {
        this.id = UUID.randomUUID();
        this.code = validateCode(code);
        this.label = validateLabel(label);
    }

    // Constructor for loading from database
    public Post(UUID id, String code, String label) {
        this.id = Objects.requireNonNull(id, "Post ID cannot be null");
        this.code = validateCode(code);
        this.label = validateLabel(label);
    }

    // Business logic methods
    public boolean isValid() {
        return code != null && label != null && !code.trim().isEmpty() && !label.trim().isEmpty();
    }

    // Validation methods
    private String validateCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Post code cannot be null or empty");
        }
        return code.trim().toUpperCase();
    }

    private String validateLabel(String label) {
        if (label == null || label.trim().isEmpty()) {
            throw new IllegalArgumentException("Post label cannot be null or empty");
        }
        return label.trim();
    }

    // Getters
    public UUID getId() { return id; }
    public String getCode() { return code; }
    public String getLabel() { return label; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
