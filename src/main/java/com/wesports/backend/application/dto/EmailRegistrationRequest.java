package com.wesports.backend.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailRegistrationRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email,
    
    String language // defaults to "en"
) {
    public String getLanguage() {
        // Default to English if not specified or invalid
        if (language == null || language.trim().isEmpty()) {
            return "en";
        }
        String lang = language.toLowerCase().trim();
        return lang.matches("^(en|fr|ar)$") ? lang : "en";
    }
}
