package com.wesports.backend.domain.repository;

import com.wesports.backend.domain.model.Language;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LanguageRepository {
    
    Language save(Language language);
    
    Optional<Language> findById(UUID languageId);
    
    Optional<Language> findByCode(String code);
    
    List<Language> findAll();
    
    List<Language> findAllActive();
    
    boolean existsByCode(String code);
    
    void delete(Language language);
    
    void deleteById(UUID languageId);
}
