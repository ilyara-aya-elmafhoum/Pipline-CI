package com.wesports.backend.application.dto;

import jakarta.validation.constraints.NotBlank;

public record PositionSelectionRequest(
    @NotBlank(message = "Position is required")
    String positionCode
) {}
