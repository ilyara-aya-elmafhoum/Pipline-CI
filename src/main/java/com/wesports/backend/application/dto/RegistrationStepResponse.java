package com.wesports.backend.application.dto;

public record RegistrationStepResponse(
    String message,
    String status,
    String nextStep,
    String registrationToken
) {
    public static RegistrationStepResponse success(String message, String nextStep) {
        return new RegistrationStepResponse(message, "success", nextStep, null);
    }
    
    public static RegistrationStepResponse successWithToken(String message, String nextStep, String token) {
        return new RegistrationStepResponse(message, "success", nextStep, token);
    }
    
    public static RegistrationStepResponse error(String message) {
        return new RegistrationStepResponse(message, "error", null, null);
    }
}