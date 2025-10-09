package com.wesports.backend.application.port;

import com.wesports.backend.application.dto.AuthResponse;
import com.wesports.backend.application.dto.EmailRegistrationRequest;
import com.wesports.backend.application.dto.OtpVerificationRequest;
import com.wesports.backend.application.dto.PasswordSetupRequest;
import com.wesports.backend.application.dto.RegistrationStepResponse;
import com.wesports.backend.application.dto.RoleSelectionRequest;
import com.wesports.backend.application.dto.ProfileFormRequest;
import com.wesports.backend.domain.valueobject.UserId;

public interface RegistrationService {
    
    /**
     * Start the registration process by sending an OTP to the provided email
     */
    RegistrationStepResponse startEmailRegistration(EmailRegistrationRequest request);
    
    /**
     * Verify the OTP sent to the email during registration
     */
    RegistrationStepResponse verifyRegistrationOtp(OtpVerificationRequest request);
    
    /**
     * Complete registration by setting up password with validated registration token
     */
    AuthResponse setupPassword(PasswordSetupRequest request);
    
        /**
     * Select user role during registration (PLAYER, CLUB, etc.)
     */
    RegistrationStepResponse selectRole(UserId userId, RoleSelectionRequest request);
    
    /**
     * Submit profile form with personal details
     */
    RegistrationStepResponse submitProfileForm(UserId userId, ProfileFormRequest request);
}
