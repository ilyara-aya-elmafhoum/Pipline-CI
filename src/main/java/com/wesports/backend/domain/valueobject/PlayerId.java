package com.wesports.backend.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object for Player identification
 */
public class PlayerId {
    private final UUID value;

    private PlayerId(UUID value) {
        this.value = Objects.requireNonNull(value, "Player ID cannot be null");
    }

    public static PlayerId of(UUID value) {
        return new PlayerId(value);
    }

    public static PlayerId generate() {
        return new PlayerId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerId playerId = (PlayerId) o;
        return Objects.equals(value, playerId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "PlayerId{" + value + '}';
    }
}
