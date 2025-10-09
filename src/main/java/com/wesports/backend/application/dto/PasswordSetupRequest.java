package com.wesports.backend.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for password setup during registration
 */
public record PasswordSetupRequest(
    
    @NotBlank(message = "Registration token is required")
    String registrationToken,

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    String password,
    
    @NotBlank(message = "Password confirmation is required")
    String confirmPassword
) {

    // Password must match confirm password
    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
