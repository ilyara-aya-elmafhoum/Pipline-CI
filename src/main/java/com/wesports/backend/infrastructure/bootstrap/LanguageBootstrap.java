package com.wesports.backend.infrastructure.bootstrap;

import com.wesports.backend.domain.model.Language;
import com.wesports.backend.domain.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * TEMPORARY: Bootstrap service to create initial languages
 * TODO: Replace with REST endpoint once ready
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LanguageBootstrap implements ApplicationRunner {
    
    private final LanguageRepository languageRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("🌍 Initializing default languages...");
        initializeDefaultLanguages();
        log.info("✅ Language initialization completed");
    }

    private void initializeDefaultLanguages() {
        // Create default languages if they don't exist
        createLanguageIfNotExists("English", "en");
        createLanguageIfNotExists("Français", "fr"); 
        createLanguageIfNotExists("العربية", "ar");
    }

    private void createLanguageIfNotExists(String name, String code) {
        if (!languageRepository.existsByCode(code)) {
            Language language = new Language(name, code);
            languageRepository.save(language);
            log.info("✅ Created language: {} ({})", name, code);
        } else {
            log.debug("Language already exists: {} ({})", name, code);
        }
    }
}
