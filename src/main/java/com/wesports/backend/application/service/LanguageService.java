package com.wesports.backend.application.service;

import com.wesports.backend.application.dto.LanguageResponse;
import com.wesports.backend.domain.model.Language;
import com.wesports.backend.domain.model.User;
import com.wesports.backend.domain.repository.LanguageRepository;
import com.wesports.backend.domain.repository.UserRepository;
import com.wesports.backend.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing user language preferences
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LanguageService {
    
    private final LanguageRepository languageRepository;
    private final UserRepository userRepository;

    /**
     * Get all active languages for selection
     */
    public List<LanguageResponse> getActiveLanguages() {

        return languageRepository.findAllActive()
                .stream()
                .map(this::toLanguageResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update user's language preference
     */
    @Transactional
    public boolean updateUserLanguage(UserId userId, String languageCode) {
        log.info("Updating language for user: {} to: {}", userId.getValue(), languageCode);
        
        try {
            // Find and validate the language
            Optional<Language> languageOpt = languageRepository.findByCode(languageCode.toLowerCase());
            if (languageOpt.isEmpty() || !languageOpt.get().isActive()) {
                log.warn("Language not found or inactive: {}", languageCode);
                return false;
            }
            
            Language language = languageOpt.get();
            
            // Since Player no longer inherits from User, always update User entity for language preference
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setPreferredLanguage(language.getId());
                userRepository.save(user);
                log.info("Updated User language to: {} ({})", language.getName(), language.getCode());
                return true;
            }
            
            log.warn("User not found: {}", userId.getValue());
            return false;
            
        } catch (Exception e) {
            log.error("Failed to update user language", e);
            return false;
        }
    }

    private LanguageResponse toLanguageResponse(Language language) {
        return new LanguageResponse(
            language.getId(),
            language.getName(),
            language.getCode(),
            language.isActive()
        );
    }
}
