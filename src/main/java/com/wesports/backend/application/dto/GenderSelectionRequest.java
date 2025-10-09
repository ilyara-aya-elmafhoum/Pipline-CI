package com.wesports.backend.application.dto;

import jakarta.validation.constraints.NotBlank;

public record GenderSelectionRequest(
    @NotBlank(message = "Gender is required")
    String gender
) {}
