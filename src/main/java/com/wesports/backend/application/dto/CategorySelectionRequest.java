package com.wesports.backend.application.dto;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;

public record CategorySelectionRequest(
    @NotEmpty(message = "At least one category must be selected")
    List<String> categoryCodes
) {}
