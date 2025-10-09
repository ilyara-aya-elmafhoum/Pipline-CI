package com.wesports.backend.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for language selection
 */
public record LanguageSelectionRequest(
    @NotBlank(message = "Language code is required")
    String languageCode
) {}
