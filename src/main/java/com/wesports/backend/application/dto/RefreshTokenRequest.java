package com.wesports.backend.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request to refresh access token using refresh token
 */
public record RefreshTokenRequest(
    @NotBlank(message = "Refresh token is required")
    String refreshToken
) {
}
