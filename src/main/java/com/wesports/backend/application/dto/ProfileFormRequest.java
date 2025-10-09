package com.wesports.backend.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for profile form submission during registration
 * Part of the application layer in hexagonal architecture
 */
public record ProfileFormRequest(
    @NotBlank(message = "First name is required")
    String firstName,
    
    @NotBlank(message = "Last name is required") 
    String lastName,
    
    @NotNull(message = "Birth date is required")
    LocalDate birthDate,
    
    @NotBlank(message = "Nationality is required")
    String nationality,
    
    @NotBlank(message = "Lieu de residence is required") 
    String lieuDeResidence, // Place of residence
    
    @NotEmpty(message = "At least one language is required")
    List<String> languages
) {}
