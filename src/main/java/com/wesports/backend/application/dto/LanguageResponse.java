package com.wesports.backend.application.dto;

import java.util.UUID;

/**
 * Response DTO for language information
 */
public record LanguageResponse(
    UUID id,
    String name,
    String code,
    boolean isActive
) {}
