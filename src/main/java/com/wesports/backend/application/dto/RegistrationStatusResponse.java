package com.wesports.backend.application.dto;

import java.util.List;

/**
 * Response for registration status inquiry
 */
public record RegistrationStatusResponse(
    String email,
    String status,
    String currentStep,
    List<String> completedSteps,
    Boolean registrationCompleted
) {
}
