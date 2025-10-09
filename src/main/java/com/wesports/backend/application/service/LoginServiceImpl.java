package com.wesports.backend.application.service;

import com.wesports.backend.application.dto.AuthResponse;
import com.wesports.backend.application.dto.LoginRequest;
import com.wesports.backend.application.dto.LogoutResponse;
import com.wesports.backend.application.port.inbound.LoginService;
import com.wesports.backend.application.port.outbound.AccessTokenService;
import com.wesports.backend.application.port.outbound.RefreshTokenService;
import com.wesports.backend.domain.model.User;
import com.wesports.backend.domain.model.UserAuthMethod;
import com.wesports.backend.domain.repository.UserRepository;
import com.wesports.backend.domain.repository.UserAuthMethodRepository;
import com.wesports.backend.domain.valueobject.AuthMethodType;
import com.wesports.backend.domain.valueobject.Email;
import com.wesports.backend.domain.valueobject.UserId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class LoginServiceImpl implements LoginService {
    
    private final UserRepository userRepository;
    private final UserAuthMethodRepository userAuthMethodRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    
    @Autowired
    public LoginServiceImpl(
            UserRepository userRepository,
            UserAuthMethodRepository userAuthMethodRepository,
            PasswordEncoder passwordEncoder,
            AccessTokenService accessTokenService,
            RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.userAuthMethodRepository = userAuthMethodRepository;
        this.passwordEncoder = passwordEncoder;
        this.accessTokenService = accessTokenService;
        this.refreshTokenService = refreshTokenService;
    }
    
    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            // Validate email format
            Email email = Email.of(request.email());
            
            // Find user by email
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return AuthResponse.error("Invalid email or password");
            }
            
            User user = userOpt.get();
            
            // Check if email is verified
            if (!user.isEmailVerified()) {
                return AuthResponse.error("Please verify your email first");
            }
            
            // Find WeSport authentication method for the user
            Optional<UserAuthMethod> authMethodOpt = userAuthMethodRepository.findByUserIdAndAuthType(
                user.getId(), 
                AuthMethodType.WESPORT
            );
            
            if (authMethodOpt.isEmpty()) {
                return AuthResponse.error("Please complete your registration first");
            }
            
            UserAuthMethod authMethod = authMethodOpt.get();
            
            // Check if auth method is active
            if (!authMethod.isActive()) {
                return AuthResponse.error("Account is inactive. Please contact support.");
            }
            
            // Verify password using UserAuthMethod
            if (authMethod.getPasswordHash() == null || authMethod.getPasswordHash().isEmpty()) {
                return AuthResponse.error("Please complete your registration first");
            }
            
            if (!passwordEncoder.matches(request.password(), authMethod.getPasswordHash())) {
                return AuthResponse.error("Invalid email or password");
            }
            
            // Generate tokens
            String accessToken = accessTokenService.generateAccessToken(user.getId().getValue().toString(), user.getEmail().getValue());
            String refreshToken = refreshTokenService.generateRefreshToken(user.getId().getValue().toString(), user.getEmail().getValue());
            
            // Return successful response with user info
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                user.getId().getValue().toString(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail().getValue(),
                user.getGender() != null ? user.getGender().toString() : null,
                user.getBirthday(),
                user.getCreatedAt(),
                user.isEmailVerified()
            );
            
            return AuthResponse.successWithTokens(
                "Login successful",
                accessToken,
                refreshToken,
                userInfo
            );
            
        } catch (IllegalArgumentException e) {
            return AuthResponse.error("Invalid email format");
        } catch (Exception e) {
            return AuthResponse.error("Login failed. Please try again.");
        }
    }
    
    @Override
    public LogoutResponse logout(String refreshToken) {
        try {
            if (refreshToken != null && !refreshToken.isEmpty()) {
                // Invalidate refresh token
                refreshTokenService.invalidateToken(refreshToken);
            }
            
            return LogoutResponse.success("Logged out successfully");
            
        } catch (Exception e) {
            return LogoutResponse.error("Logout failed. Please try again.");
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        try {
            if (refreshToken == null || refreshToken.isEmpty()) {
                return AuthResponse.error("Refresh token is required");
            }
            
            // Validate refresh token using RefreshTokenService
            if (!refreshTokenService.validateRefreshToken(refreshToken)) {
                return AuthResponse.error("Invalid or expired refresh token");
            }
            
            // Extract user information from refresh token
            String userId = refreshTokenService.extractUserIdFromToken(refreshToken);
            String email = refreshTokenService.extractEmailFromToken(refreshToken);
            
            if (userId == null || email == null) {
                return AuthResponse.error("Invalid refresh token");
            }
            
            // Find user to ensure they still exist
            Optional<User> userOptional = userRepository.findById(UserId.of(UUID.fromString(userId)));
            if (userOptional.isEmpty()) {
                return AuthResponse.error("User not found");
            }
            
            User user = userOptional.get();
            
            // Generate new access token
            String newAccessToken = accessTokenService.generateAccessToken(user.getId().getValue().toString(), user.getEmail().getValue());
            
            // Generate new refresh token (token rotation for security)
            String newRefreshToken = refreshTokenService.generateRefreshToken(userId, email);
            
            // Invalidate old refresh token
            refreshTokenService.invalidateToken(refreshToken);
            
            // Create user info for response
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                user.getId().getValue().toString(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail().getValue(),
                user.getGender() != null ? user.getGender().toString() : null,
                user.getBirthday(),
                user.getCreatedAt(),
                user.isEmailVerified()
            );
            
            return AuthResponse.success(
                "Token refreshed successfully",
                newAccessToken,
                newRefreshToken,
                900L, // 15 minutes expiry
                userInfo
            );
            
        } catch (Exception e) {
            return AuthResponse.error("Token refresh failed. Please try again.");
        }
    }
}
