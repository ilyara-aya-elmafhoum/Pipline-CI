package com.wesports.backend.domain.valueobject;

import java.util.*;

public class AuthProviders {
    private final Set<String> providers;

    private AuthProviders(Set<String> providers) {
        this.providers = new HashSet<>(providers);
    }

    public static AuthProviders of(Set<String> providers) {
        if (providers == null) {
            return new AuthProviders(new HashSet<>());
        }
        
        Set<String> validatedProviders = new HashSet<>();
        for (String provider : providers) {
            if (provider != null && !provider.trim().isEmpty()) {
                validatedProviders.add(provider.trim().toLowerCase());
            }
        }
        
        return new AuthProviders(validatedProviders);
    }

    public static AuthProviders empty() {
        return new AuthProviders(new HashSet<>());
    }

    public static AuthProviders local() {
        return new AuthProviders(Set.of("local"));
    }

    public AuthProviders addProvider(String provider) {
        if (provider == null || provider.trim().isEmpty()) {
            return this;
        }
        
        Set<String> newProviders = new HashSet<>(this.providers);
        newProviders.add(provider.trim().toLowerCase());
        return new AuthProviders(newProviders);
    }

    public AuthProviders removeProvider(String provider) {
        if (provider == null || provider.trim().isEmpty()) {
            return this;
        }
        
        Set<String> newProviders = new HashSet<>(this.providers);
        newProviders.remove(provider.trim().toLowerCase());
        return new AuthProviders(newProviders);
    }

    public boolean hasProvider(String provider) {
        if (provider == null || provider.trim().isEmpty()) {
            return false;
        }
        return providers.contains(provider.trim().toLowerCase());
    }

    public Set<String> getProviders() {
        return new HashSet<>(providers);
    }

    public String[] toArray() {
        return providers.toArray(new String[0]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthProviders that = (AuthProviders) o;
        return Objects.equals(providers, that.providers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providers);
    }

    @Override
    public String toString() {
        return String.join(", ", providers);
    }
}
