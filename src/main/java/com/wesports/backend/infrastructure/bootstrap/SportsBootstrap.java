package com.wesports.backend.infrastructure.bootstrap;

import com.wesports.backend.domain.model.Sport;
import com.wesports.backend.domain.repository.SportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

/**
 * Bootstrap component to initialize default sports in the database
 */
@Component
@Order(2) // Run after LanguageBootstrap (which is Order 1)
public class SportsBootstrap implements CommandLineRunner {
    
    private static final Logger log = Logger.getLogger(SportsBootstrap.class.getName());
    
    private final SportRepository sportRepository;
    
    @Autowired
    public SportsBootstrap(SportRepository sportRepository) {
        this.sportRepository = sportRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        log.info("🏈 Initializing default sports...");
        
        try {
            // Check and create Football sport
            if (sportRepository.findByCode("FOOTBALL").isEmpty()) {
                Sport football = new Sport("Football", "FOOTBALL", "Association football (soccer)");
                sportRepository.save(football);
                log.info("✅ Created Football sport: " + football.getId().getValue());
            } else {
                log.info("✅ Football sport already exists");
            }
            
            log.info("✅ Sports initialization completed");
            
        } catch (Exception e) {
            log.severe("❌ Failed to initialize sports: " + e.getMessage());
            throw e;
        }
    }
}
