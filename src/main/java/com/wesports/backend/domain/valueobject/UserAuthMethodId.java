package com.wesports.backend.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object for UserAuthMethod ID
 */
public class UserAuthMethodId {
    private final UUID value;

    private UserAuthMethodId(UUID value) {
        this.value = Objects.requireNonNull(value, "UserAuthMethodId cannot be null");
    }

    public static UserAuthMethodId of(UUID value) {
        return new UserAuthMethodId(value);
    }

    public static UserAuthMethodId generate() {
        return new UserAuthMethodId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAuthMethodId that = (UserAuthMethodId) o;
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
