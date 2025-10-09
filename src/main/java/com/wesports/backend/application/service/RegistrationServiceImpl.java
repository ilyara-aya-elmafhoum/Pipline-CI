package com.wesports.backend.application.service;

import com.wesports.backend.application.dto.AuthResponse;
import com.wesports.backend.application.dto.EmailRegistrationRequest;
import com.wesports.backend.application.dto.OtpVerificationRequest;
import com.wesports.backend.application.dto.PasswordSetupRequest;
import com.wesports.backend.application.dto.RegistrationStepResponse;
import com.wesports.backend.application.dto.RoleSelectionRequest;
import com.wesports.backend.application.dto.ProfileFormRequest;
import com.wesports.backend.application.port.EmailService;
import com.wesports.backend.application.port.RegistrationService;
import com.wesports.backend.domain.model.Language;
import com.wesports.backend.domain.model.OTP;
import com.wesports.backend.domain.model.User;
import com.wesports.backend.domain.model.Player;
import com.wesports.backend.domain.model.Sport;
import com.wesports.backend.domain.repository.PlayerRepository;
import com.wesports.backend.domain.repository.SportRepository;
import com.wesports.backend.application.port.inbound.PlayerSportService;

import com.wesports.backend.domain.repository.OTPRepository;
import com.wesports.backend.domain.repository.UserRepository;

import com.wesports.backend.domain.repository.LanguageRepository;

import com.wesports.backend.domain.valueobject.Email;
import com.wesports.backend.domain.valueobject.RegistrationStep;
import com.wesports.backend.domain.valueobject.UserId;
import com.wesports.backend.domain.model.UserAuthMethod;
import com.wesports.backend.domain.repository.UserAuthMethodRepository;
import com.wesports.backend.domain.valueobject.UserRole;
import com.wesports.backend.infrastructure.security.AccessTokenService;
import com.wesports.backend.infrastructure.security.JwtTokenService;
import com.wesports.backend.infrastructure.security.RefreshTokenService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final UserAuthMethodRepository userAuthMethodRepository;
    private final PlayerRepository playerRepository;
    private final SportRepository sportRepository;
    private final PlayerSportService playerSportService;
    private final OTPRepository otpRepository;
    private final LanguageRepository languageRepository;
    private final EmailService emailService;
    private final JwtTokenService jwtTokenService;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    // Simple in-memory rate limiting
    private final java.util.concurrent.ConcurrentHashMap<String, java.util.concurrent.atomic.AtomicInteger> otpAttempts = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.concurrent.ConcurrentHashMap<String, Long> lastAttemptTime = new java.util.concurrent.ConcurrentHashMap<>();
    
    private static final int MAX_OTP_REQUESTS_PER_HOUR = 5;
    private static final int MAX_REVERIFY_ATTEMPTS = 3;

    @Override
    @Transactional
    public RegistrationStepResponse startEmailRegistration(EmailRegistrationRequest request) {
        try {
            // Validate email format
            Email email = Email.of(request.email());
            String languageCode = request.getLanguage();

            // Rate limiting check
            if (!checkRateLimit(email.getValue())) {
                log.warn("Rate limit exceeded for email: {}", email.getValue());
                return RegistrationStepResponse.error("Too many OTP requests. Please try again later.");
            }

            // Check if user already exists
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                log.warn("User already exists for email: {}", email.getValue());
                return RegistrationStepResponse.error("User with this email already exists. Please login instead.");
            }

            // For email-based registration, we'll use the email as a temporary identifier
            // Create a temporary user ID based on email hash for OTP storage
            UserId tempUserId = createTempUserIdFromEmail(email);
            log.info("Generated tempUserId: {}", tempUserId.getValue());

            // Delete any existing registration OTPs for this email
            otpRepository.findByUserIdAndType(tempUserId, "REGISTRATION")
                .ifPresent(existingOtp -> {
                    log.info("Deleting existing OTP for tempUserId: {}", tempUserId.getValue());
                    otpRepository.delete(existingOtp);
                });

            // Create new OTP using constructor with language code (it generates its own code)
            OTP otp = new OTP(tempUserId, "REGISTRATION", languageCode);
            log.info("Created OTP with code: {} for tempUserId: {}", otp.getOtpCode(), tempUserId.getValue());

            // Save OTP
            OTP savedOtp = otpRepository.save(otp);
            log.info("Saved OTP - ID: {}, Code: {}, UserId: {}", savedOtp.getId(), savedOtp.getOtpCode(), savedOtp.getUserId().getValue());

            // Send email
            emailService.sendRegistrationOtp(email, otp.getOtpCode(), languageCode);

            log.info("Registration OTP sent to email: {} in language: {}", email.getValue(), languageCode);


            return RegistrationStepResponse.success(
                "OTP sent to your email. Please verify to continue registration.",
                "verify-otp"
            );

        } catch (IllegalArgumentException e) {
            log.error("Invalid email format: {}", request.email(), e);
            return RegistrationStepResponse.error("Invalid email format");
        } catch (Exception e) {
            log.error("Error starting email registration for: {}", request.email(), e);
            return RegistrationStepResponse.error("Failed to send OTP. Please try again.");
        }
    }

    @Override
    @Transactional
    public RegistrationStepResponse verifyRegistrationOtp(OtpVerificationRequest request) {

        log.info("Raw email: {}", request.email());
        log.info("Raw OTP: {}", request.otp());
        
        try {
            // Validate email format
            Email email = Email.of(request.email());
            log.info("Validated email: {}", email.getValue());
            
            // Create temporary user ID from email for lookup
            UserId tempUserId = createTempUserIdFromEmail(email);
            log.info("Generated tempUserId for lookup: {}", tempUserId.getValue());

            // Find valid OTP
            Optional<OTP> otpOpt = otpRepository.findValidOTPByUserIdAndType(tempUserId, "REGISTRATION");
            log.info("OTP lookup result: {}", otpOpt.isPresent() ? "FOUND" : "NOT FOUND");
            
            if (otpOpt.isEmpty()) {
                log.warn("No valid OTP found for tempUserId: {}", tempUserId.getValue());
                return RegistrationStepResponse.error("Invalid or expired OTP");
            }

            OTP otp = otpOpt.get();
            log.info("Found OTP - ID: {}, Code: {}, UserId: {}, Expired: {}, Attempts: {}", 
                    otp.getId(), otp.getOtpCode(), otp.getUserId().getValue(), 
                    otp.isExpired(), otp.getAttempts());

            // Check if OTP is expired
            if (otp.isExpired()) {
                log.warn("OTP is expired for tempUserId: {}", tempUserId.getValue());
                otpRepository.delete(otp);
                return RegistrationStepResponse.error("OTP has expired. Please request a new one.");
            }

            // Check if too many attempts
            if (otp.isMaxAttemptsReached()) {
                log.warn("Max attempts reached for OTP tempUserId: {}", tempUserId.getValue());
                otpRepository.delete(otp);
                return RegistrationStepResponse.error("Too many OTP attempts. Please request a new one.");
            }

            // Verify OTP code using domain method
            log.info("Verifying OTP code: {} against stored code: {}", request.otp(), otp.getOtpCode());
            if (!otp.verify(request.otp())) {
                log.warn("OTP verification failed. Provided: {}, Expected: {}", request.otp(), otp.getOtpCode());
                // Save the updated OTP with incremented attempts
                otpRepository.save(otp);
                return RegistrationStepResponse.error("Invalid OTP. Please try again.");
            }

            log.info("OTP verification successful!");
            // OTP is valid - delete it (one-time use)
            otpRepository.delete(otp);

            // Check if user already exists with a password (account already created)
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                log.warn("User already exists with email: {}. Must login instead.", email.getValue());
                return RegistrationStepResponse.error("Account already exists. Please login instead.");
            }

            // Check for any existing EMAIL_VERIFIED records (indicates re-verification)
            Optional<OTP> existingVerified = otpRepository.findByUserIdAndType(tempUserId, "EMAIL_VERIFIED");
            if (existingVerified.isPresent()) {
                // This is a re-verification attempt - check limits
                if (!checkReverifyLimit(email.getValue())) {
                    log.warn("Re-verification limit exceeded for email: {}", email.getValue());
                    return RegistrationStepResponse.error("Too many re-verification attempts. Please try again later.");
                }
                incrementReverifyAttempts(email.getValue());
            }

            // Delete any existing EMAIL_VERIFIED records for this user (rotation on re-verify)
            otpRepository.findByUserIdAndType(tempUserId, "EMAIL_VERIFIED")
                .ifPresent(existingVerifiedRecord -> {
                    log.info("Deleting existing EMAIL_VERIFIED record for rotation: {}", tempUserId.getValue());
                    otpRepository.delete(existingVerifiedRecord);
                });

            // Create basic User record for the verified email (without password yet)
            // Pass the OTP to extract user's language choice
            User newUser = createBasicUserRecord(tempUserId, email, otp);
            userRepository.save(newUser);
            log.info("Created basic User record for userId: {} email: {}", tempUserId.getValue(), email.getValue());

            // Create EMAIL_VERIFIED record with JWT ID for single-use validation
            String jti = java.util.UUID.randomUUID().toString();
            OTP emailVerifiedRecord = createEmailVerifiedRecord(tempUserId, jti);
            otpRepository.save(emailVerifiedRecord);

            // Generate short-lived JWT token (5 minutes)
            String registrationToken = jwtTokenService.generateRegistrationToken(tempUserId, jti);

            log.info("Email verified successfully for: {}", email.getValue());
            log.info("Created EMAIL_VERIFIED record with JTI: {}", jti);


            return RegistrationStepResponse.successWithToken(
                "Email verified successfully. Please set up your password within 5 minutes.",
                "setup-password",
                registrationToken
            );

        } catch (IllegalArgumentException e) {
            log.error("Invalid email format: {}", request.email(), e);
            return RegistrationStepResponse.error("Invalid email format");
        } catch (Exception e) {
            log.error("Error verifying OTP for email: {}", request.email(), e);
            return RegistrationStepResponse.error("Failed to verify OTP. Please try again.");
        }
    }

    /**
     * Create a deterministic temporary user ID from email for OTP storage
     * This allows us to link OTPs to emails before user registration is complete
     */
    private UserId createTempUserIdFromEmail(Email email) {
        // Normalize email consistently: trim and lowercase
        String normalized = email.getValue().trim().toLowerCase();
        
        // Create a deterministic UUID from normalized email for temporary storage
        java.util.UUID uuid = java.util.UUID.nameUUIDFromBytes(
            ("temp_registration_" + normalized).getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );
        return UserId.of(uuid);
    }

    /**
     * Create EMAIL_VERIFIED record with custom JWT ID
     * This uses reflection to set the OTP code after construction
     */
    private OTP createEmailVerifiedRecord(UserId userId, String jti) {
        try {
            // Create the OTP with EMAIL_VERIFIED type
            OTP emailVerified = new OTP(userId, "EMAIL_VERIFIED");
            
            // Use reflection to set the JTI as the OTP code
            java.lang.reflect.Field otpCodeField = OTP.class.getDeclaredField("otpCode");
            otpCodeField.setAccessible(true);
            otpCodeField.set(emailVerified, jti);
            
            return emailVerified;
        } catch (Exception e) {
            log.error("Failed to create EMAIL_VERIFIED record with JTI", e);
            throw new RuntimeException("Failed to create verification record", e);
        }
    }



    /**
     * Create a basic User record with language from OTP (user's chosen language)
     * Uses the language code stored in OTP during registration
     */
    private User createBasicUserRecord(UserId userId, Email email, OTP otp) {
        // Get user's chosen language from OTP, fallback to English if not specified
        UUID languageId = findLanguageIdFromOtp(otp);
        
        // Create User with minimal required fields - we'll update with full profile later
        User user = new User(
            userId,
            "User",
            "Name",  
            email,
            null, // PhoneNumber - will be set during profile completion
            null, // Gender - will be set during profile completion
            null, // Birthday - will be set during profile completion
            languageId, // Use user's chosen language from OTP
            java.time.LocalDateTime.now(),
            RegistrationStep.EMAIL_VERIFICATION // ✅ Start with email verification step
        );
        
        // Mark email as verified since OTP verification passed
        user.setEmailVerified(true);
        user.setUpdatedAt(java.time.LocalDateTime.now());
        
        return user;
    }

    /**
     * Find language ID from OTP's stored language code
     * Falls back to English if OTP has no language or language is inactive
     */
    private UUID findLanguageIdFromOtp(OTP otp) {
        // Check if OTP has a language code stored
        String languageCode = otp.getLanguageCode();
        
        if (languageCode != null && !languageCode.trim().isEmpty()) {
            // Try to find the language by code
            Optional<Language> languageOpt =
                languageRepository.findByCode(languageCode.toLowerCase());
            
            if (languageOpt.isPresent() && languageOpt.get().isActive()) {
                log.info("Using user's chosen language: {} ({})", languageOpt.get().getName(), languageCode);
                return languageOpt.get().getId();
            } else {
                log.warn("User's chosen language '{}' not found or inactive, falling back to default", languageCode);
            }
        }
        
        // Fallback to default language (English)
        log.info("No valid language in OTP, using default language");
        return findDefaultLanguageId();
    }

    /**
     * Find the default language ID (English)
     * Falls back to first active language if English not found
     */
    private UUID findDefaultLanguageId() {
        // Try to find English first
        Optional<Language> english = languageRepository.findByCode("en");
        if (english.isPresent() && english.get().isActive()) {
            return english.get().getId();
        }
        
        // Fallback to first active language
        java.util.List<Language> activeLanguages = languageRepository.findAllActive();
        if (!activeLanguages.isEmpty()) {
            return activeLanguages.get(0).getId();
        }
        
        // This shouldn't happen with our bootstrap, but return null if no languages exist
        log.warn("No active languages found! Language bootstrap may have failed.");
        return null;
    }

    /**
     * Check rate limiting for OTP requests
     * Allows MAX_OTP_REQUESTS_PER_HOUR requests per hour per email
     */
    private boolean checkRateLimit(String email) {
        String key = "otp_" + email.toLowerCase();
        long now = System.currentTimeMillis();
        long oneHourAgo = now - java.time.Duration.ofHours(1).toMillis();
        
        // Clean up old entries
        lastAttemptTime.entrySet().removeIf(entry -> entry.getValue() < oneHourAgo);
        otpAttempts.entrySet().removeIf(entry -> {
            String entryKey = entry.getKey();
            Long lastTime = lastAttemptTime.get(entryKey);
            return lastTime == null || lastTime < oneHourAgo;
        });
        
        // Check current attempts
        java.util.concurrent.atomic.AtomicInteger attempts = otpAttempts.computeIfAbsent(key, k -> new java.util.concurrent.atomic.AtomicInteger(0));
        
        if (attempts.get() >= MAX_OTP_REQUESTS_PER_HOUR) {
            log.warn("Rate limit exceeded for email: {}. Attempts: {}", email, attempts.get());
            return false;
        }
        
        // Increment attempts and update timestamp
        attempts.incrementAndGet();
        lastAttemptTime.put(key, now);
        
        log.info("Rate limit check passed for email: {}. Attempts: {}/{}", email, attempts.get(), MAX_OTP_REQUESTS_PER_HOUR);
        return true;
    }

    /**
     * Check re-verification attempts to prevent abuse
     */
    private boolean checkReverifyLimit(String email) {
        String key = "reverify_" + email.toLowerCase();
        long now = System.currentTimeMillis();
        long oneHourAgo = now - java.time.Duration.ofHours(1).toMillis();
        
        // Clean up old entries
        lastAttemptTime.entrySet().removeIf(entry -> entry.getValue() < oneHourAgo);
        otpAttempts.entrySet().removeIf(entry -> {
            String entryKey = entry.getKey();
            Long lastTime = lastAttemptTime.get(entryKey);
            return lastTime == null || lastTime < oneHourAgo;
        });
        
        // Check current re-verify attempts
        java.util.concurrent.atomic.AtomicInteger attempts = otpAttempts.computeIfAbsent(key, k -> new java.util.concurrent.atomic.AtomicInteger(0));
        
        if (attempts.get() >= MAX_REVERIFY_ATTEMPTS) {
            log.warn("Re-verify limit exceeded for email: {}. Attempts: {}", email, attempts.get());
            return false;
        }
        
        return true;
    }

    /**
     * Increment re-verification counter
     */
    private void incrementReverifyAttempts(String email) {
        String key = "reverify_" + email.toLowerCase();
        java.util.concurrent.atomic.AtomicInteger attempts = otpAttempts.computeIfAbsent(key, k -> new java.util.concurrent.atomic.AtomicInteger(0));
        attempts.incrementAndGet();
        lastAttemptTime.put(key, System.currentTimeMillis());
        log.info("Incremented re-verify attempts for email: {}. Count: {}/{}", email, attempts.get(), MAX_REVERIFY_ATTEMPTS);
    }

    /**
     * Complete registration by setting up password with validated registration token
     * Follows hexagonal architecture by validating token and creating authenticated user
     */
    @Override
    @Transactional
    public AuthResponse setupPassword(PasswordSetupRequest request) {

        
        try {
            // Validate passwords match
            if (!request.passwordsMatch()) {
                log.warn("Password confirmation mismatch");
                return AuthResponse.error("Password and confirmation do not match");
            }
            
            // Validate and extract user ID from registration token
            Claims claims = jwtTokenService.validateToken(request.registrationToken());
            if (claims == null) {
                log.warn("Invalid or expired registration token");
                return AuthResponse.error("Invalid or expired registration token");
            }
            
            // Verify it's a registration token
            if (!jwtTokenService.isRegistrationToken(request.registrationToken())) {
                log.warn("Token is not a registration token");
                return AuthResponse.error("Invalid token type");
            }
            
            // Extract user information
            String userIdStr = claims.getSubject();
            UserId userId = UserId.of(UUID.fromString(userIdStr));
            
            log.info("Setting up password for userId: {}", userId.getValue());
            
            // Find the user who needs password setup
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.error("User not found for registration token: {}", userId.getValue());
                return AuthResponse.error("User not found");
            }
            
            User user = userOpt.get();
            
            // Verify user is in EMAIL_VERIFIED state (from OTP verification)
            if (!user.isEmailVerified()) {
                log.warn("User email not verified for userId: {}", userId.getValue());
                return AuthResponse.error("Email not verified");
            }
            
            // Check if password is already set
            if (user.getPasswordHash() != null && !user.getPasswordHash().trim().isEmpty()) {
                log.warn("Password already set for userId: {}", userId.getValue());
                return AuthResponse.error("Password already configured");
            }
            
            // Hash and set the password
            String hashedPassword = passwordEncoder.encode(request.password());
            user.setPasswordHash(hashedPassword);
            user.setUpdatedAt(LocalDateTime.now());
            
            // Save updated user
            userRepository.save(user);
            log.info("Password set successfully for userId: {}", userId.getValue());
            
            // Create UserAuthMethod record for WeSport local authentication
            try {
                UserAuthMethod localAuthMethod = new UserAuthMethod(
                    userId,
                    user.getEmail(),
                    hashedPassword
                );
                userAuthMethodRepository.save(localAuthMethod);
                log.info("Created WeSport auth method for userId: {}", userId.getValue());
            } catch (Exception e) {
                log.error("Failed to create UserAuthMethod for userId: {}, this could cause login issues", userId.getValue(), e);
                // Note: UserAuthMethod creation is now required for proper authentication
                // If this fails, the user might not be able to log in with the new architecture
                throw new RuntimeException("Failed to create authentication method. Registration aborted.", e);
            }
            
            // Generate access and refresh tokens
            String accessToken = accessTokenService.generateAccessToken(userId, user.getEmail().getValue());
            String refreshToken = refreshTokenService.generateRefreshToken(userId, user.getEmail().getValue());
            
            // Create user info for response
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                userId.getValue().toString(),
                user.getEmail().getValue(),
                user.getFirstName(),
                user.getLastName(),
                user.isEmailVerified()
            );
            
            log.info("Registration completed successfully for userId: {} email: {}", 
                userId.getValue(), user.getEmail().getValue());
            
            return AuthResponse.success(
                "Registration completed successfully",
                accessToken,
                refreshToken,
                accessTokenService.getExpiryInSeconds(),
                userInfo
            );
            
        } catch (Exception e) {
            log.error("Failed to setup password", e);
            return AuthResponse.error("Registration failed. Please try again.");
        }
    }

    /**
     * Select user role during registration - creates Player inheritance if PLAYER role selected
     */
    @Override
    @Transactional
    public RegistrationStepResponse selectRole(UserId userId, RoleSelectionRequest request) {
        try {

            log.info("UserId: {} Role: {}", userId.getValue(), request.role());
            
            // Only allow PLAYER role for now
            if (request.role() != UserRole.PLAYER) {
                log.warn("Role selection rejected - only PLAYER role supported: {}", request.role());
                return RegistrationStepResponse.error("For now we handle only player");
            }
            
            // Find and update user
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.error("User not found for role selection: {}", userId.getValue());
                return RegistrationStepResponse.error("User not found");
            }
            
            User user = userOpt.get();
            user.setUserRole(request.role());
            userRepository.save(user);
            
            // ✅ NEW FIX: Create Player entity + PlayerSport junction immediately when PLAYER role selected
            if (request.role() == UserRole.PLAYER) {
                log.info("PLAYER role selected - Creating Player entity and PlayerSport junction immediately");
                
                // Check if Player entity already exists
                Optional<Player> existingPlayer = playerRepository.findById(userId);
                if (existingPlayer.isEmpty()) {
                    // 1. Create minimal Player entity with User ID as Player ID (no position/category yet)
                    Player player = new Player(userId); // Uses userId as playerId to align with repository
                    Player savedPlayer = playerRepository.save(player);
                    log.info("Player entity created with ID: {}", savedPlayer.getId().getValue());
                    
                    // 2. Find or create Football sport (default sport for players)
                    Optional<Sport> footballOpt = sportRepository.findByCode("FOOTBALL");
                    Sport football;
                    if (footballOpt.isPresent()) {
                        football = footballOpt.get();
                        log.info("Found existing Football sport with ID: {}", football.getId().getValue());
                    } else {
                        // Create Football sport if it doesn't exist
                        log.info("Football sport not found - creating new Football sport");
                        football = new Sport("Football", "FOOTBALL", "Association football (soccer)");
                        football = sportRepository.save(football);
                        log.info("Created new Football sport with ID: {}", football.getId().getValue());
                    }
                    
                    // 3. Create PlayerSport junction table record
                    playerSportService.createAssociation(userId, savedPlayer.getId(), football.getId());
                    log.info("PlayerSport junction created: User {} <-> Player {} <-> Sport FOOTBALL", 
                            userId.getValue(), savedPlayer.getId().getValue());
                } else {
                    log.info("Player entity already exists for userId: {}", userId.getValue());
                }
            }
            
            log.info("Role selection completed successfully for userId: {} role: {}", 
                userId.getValue(), request.role());
            
            return RegistrationStepResponse.success(
                "Role selected successfully",
                "gender-selection"
            );
            
        } catch (Exception e) {
            log.error("Role selection failed for userId: {}", userId.getValue(), e);
            return RegistrationStepResponse.error("Failed to select role. Please try again.");
        }
    }

    /**
     * Submit profile form with personal details
     */
    @Override
    @Transactional
    public RegistrationStepResponse submitProfileForm(UserId userId, ProfileFormRequest request) {
        try {

            log.info("UserId: {}", userId.getValue());
            
            // Since Player is now independent, always update User entity for profile information
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.error("User not found for profile form: {}", userId.getValue());
                return RegistrationStepResponse.error("User not found");
            }
            
            User user = userOpt.get();
            
            // Update profile information
            user.setFirstName(request.firstName());
            user.setLastName(request.lastName());
            user.setBirthday(request.birthDate());
            user.setNationality(request.nationality());
            user.setLieuDeResidence(request.lieuDeResidence());
            user.setLanguages(request.languages());
            
            userRepository.save(user);
            log.info("Profile form submitted successfully for User userId: {}", userId.getValue());
            
            return RegistrationStepResponse.success(
                "Profile updated successfully",
                "categories-selection"
            );
            
        } catch (Exception e) {
            log.error("Profile form submission failed for userId: {}", userId.getValue(), e);
            return RegistrationStepResponse.error("Failed to update profile. Please try again.");
        }
    }
}
