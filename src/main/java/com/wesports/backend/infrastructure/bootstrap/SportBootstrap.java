package com.wesports.backend.infrastructure.bootstrap;

import com.wesports.backend.domain.model.Sport;
import com.wesports.backend.domain.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Bootstrap service to create initial sports data
 * Ensures Football sport exists for Player creation during role selection
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1) // Run before other bootstraps that might need sports
public class SportBootstrap implements ApplicationRunner {
    
    private final SportRepository sportRepository;

    @Override
    public void run(ApplicationArguments args) {
        log.info("⚽ Initializing default sports...");
        initializeDefaultSports();
        log.info("✅ Sport initialization completed");
    }

    private void initializeDefaultSports() {
        // Create Football sport if it doesn't exist
        createSportIfNotExists("Football", "FOOTBALL", "Association football (soccer)");
    }

    private void createSportIfNotExists(String name, String code, String description) {
        if (!sportRepository.existsByCode(code)) {
            Sport sport = new Sport(name, code, description);
            sportRepository.save(sport);
            log.info("Created sport: {} with code: {}", name, code);
        } else {
            log.debug("Sport already exists: {}", code);
        }
    }
}
