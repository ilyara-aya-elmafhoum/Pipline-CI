package com.wesports.backend.domain.valueobject;

/**
 * Value object representing registration steps
 */
public enum RegistrationStep {
    EMAIL_VERIFICATION,
    PASSWORD_SETUP,
    ROLE_SELECTION,
    PROFILE_FORM,
    ONBOARDING,
    COMPLETED;
    
    public static RegistrationStep fromString(String step) {
        try {
            return RegistrationStep.valueOf(step.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid registration step: " + step);
        }
    }
    
    public String getCode() {
        return this.name();
    }
    
    public RegistrationStep getNextStep() {
        return switch (this) {
            case EMAIL_VERIFICATION -> PASSWORD_SETUP;
            case PASSWORD_SETUP -> ROLE_SELECTION;
            case ROLE_SELECTION -> PROFILE_FORM;
            case PROFILE_FORM -> ONBOARDING;
            case ONBOARDING -> COMPLETED;
            case COMPLETED -> COMPLETED; // Already completed
        };
    }
    
    public boolean isCompleted() {
        return this == COMPLETED;
    }
}
