package com.wesports.backend.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object representing a UserAuthMethod identifier
 * Part of the domain layer in hexagonal architecture
 */
public class AuthMethodId {
    
    private final UUID value;

    private AuthMethodId(UUID value) {
        this.value = Objects.requireNonNull(value, "AuthMethod ID cannot be null");
    }

    public static AuthMethodId generate() {
        return new AuthMethodId(UUID.randomUUID());
    }

    public static AuthMethodId of(UUID value) {
        return new AuthMethodId(value);
    }

    public static AuthMethodId of(String value) {
        try {
            return new AuthMethodId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid AuthMethod ID format: " + value, e);
        }
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthMethodId that = (AuthMethodId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
