package com.wesports.backend.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.time.LocalDate;

public record PlayerProfileRequest(
    @NotBlank(message = "First name is required")
    String firstName,
    
    @NotBlank(message = "Last name is required")
    String lastName,
    
    @NotNull(message = "Birthday is required")
    LocalDate birthday,
    
    @DecimalMin(value = "1.0", message = "Height must be at least 1.0 meters")
    @DecimalMax(value = "3.0", message = "Height must not exceed 3.0 meters")
    Float height,
    
    @DecimalMin(value = "30.0", message = "Weight must be at least 30 kg")
    @DecimalMax(value = "200.0", message = "Weight must not exceed 200 kg")
    Float weight,
    
    String profilePhotoUrl
) {}
