package com.wesports.backend.application.port;

import com.wesports.backend.domain.valueobject.Email;

public interface EmailService {
    
    /**
     * Send OTP to email for registration verification
     */
    void sendRegistrationOtp(Email email, String otp, String language);
    
    /**
     * Send welcome email after successful registration
     */
    void sendWelcomeEmail(Email email, String firstName, String language);
}
