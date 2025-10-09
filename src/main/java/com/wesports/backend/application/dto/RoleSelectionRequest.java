package com.wesports.backend.application.dto;

import com.wesports.backend.domain.valueobject.UserRole;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for role selection during registration
 * Part of the application layer in hexagonal architecture
 */
public record RoleSelectionRequest(
    @NotNull(message = "Role is required")
    UserRole role
) {
    public String roleCode() {
        return role != null ? role.getCode() : null;
    }
}
