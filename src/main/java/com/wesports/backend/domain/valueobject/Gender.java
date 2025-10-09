package com.wesports.backend.domain.valueobject;

public enum Gender {
    MALE,
    FEMALE;

    public static Gender fromString(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender cannot be null or empty");
        }
        
        try {
            return Gender.valueOf(gender.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid gender value: " + gender + 
                ". Valid values are: MALE, FEMALE");
        }
    }
}
