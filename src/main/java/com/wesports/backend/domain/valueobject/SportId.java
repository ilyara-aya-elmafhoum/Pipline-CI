package com.wesports.backend.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object for Sport identification
 */
public class SportId {
    private final UUID value;

    private SportId(UUID value) {
        this.value = Objects.requireNonNull(value, "Sport ID cannot be null");
    }

    public static SportId of(UUID value) {
        return new SportId(value);
    }

    public static SportId generate() {
        return new SportId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SportId sportId = (SportId) o;
        return Objects.equals(value, sportId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "SportId{" + value + '}';
    }
}
