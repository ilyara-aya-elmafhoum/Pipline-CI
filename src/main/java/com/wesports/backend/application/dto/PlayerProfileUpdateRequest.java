package com.wesports.backend.application.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.time.LocalDate;

/**
 * DTO for updating player profile
 * All fields are optional - only present fields will be updated
 */
public record PlayerProfileUpdateRequest(
    String firstName,
    String lastName,
    String gender,        // MALE, FEMALE
    LocalDate birthday,
    String nationality,
    String lieuDeResidence,
    
    @DecimalMin(value = "1.0", message = "Height must be at least 1.0 meters")
    @DecimalMax(value = "3.0", message = "Height must not exceed 3.0 meters")
    Float height,
    
    @DecimalMin(value = "30.0", message = "Weight must be at least 30 kg")
    @DecimalMax(value = "200.0", message = "Weight must not exceed 200 kg")
    Float weight,
    
    String profilePhotoUrl,
    String positionCode,  // GK, CB, LB, RB, etc.
    String preferredFoot  // LEFT, RIGHT, BOTH
) {}
