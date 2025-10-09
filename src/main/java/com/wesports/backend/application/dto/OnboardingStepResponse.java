package com.wesports.backend.application.dto;

public record OnboardingStepResponse(
    String message,
    String status,
    String nextStep,
    Object data
) {
    public static OnboardingStepResponse success(String message, String nextStep) {
        return new OnboardingStepResponse(message, "success", nextStep, null);
    }
    
    public static OnboardingStepResponse successWithData(String message, String nextStep, Object data) {
        return new OnboardingStepResponse(message, "success", nextStep, data);
    }
    
    public static OnboardingStepResponse error(String message) {
        return new OnboardingStepResponse(message, "error", null, null);
    }
    
    public static OnboardingStepResponse completed(String message) {
        return new OnboardingStepResponse(message, "completed", null, null);
    }
}
