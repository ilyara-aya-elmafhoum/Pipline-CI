package com.wesports.backend.infrastructure.persistence.repository;

import com.wesports.backend.domain.model.Language;
import com.wesports.backend.domain.repository.LanguageRepository;
import com.wesports.backend.infrastructure.persistence.entity.LanguageEntity;
import com.wesports.backend.infrastructure.persistence.jpa.SpringLanguageRepository;
import com.wesports.backend.infrastructure.persistence.mapper.LanguageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class LanguageRepositoryAdapter implements LanguageRepository {
    
    private final SpringLanguageRepository springLanguageRepository;
    private final LanguageMapper languageMapper;

    @Override
    public Language save(Language language) {
        log.debug("Saving language: {}", language.getCode());
        LanguageEntity entity = languageMapper.toEntity(language);
        LanguageEntity savedEntity = springLanguageRepository.save(entity);
        return languageMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Language> findById(UUID languageId) {
        log.debug("Finding language by ID: {}", languageId);
        return springLanguageRepository.findById(languageId)
                .map(languageMapper::toDomain);
    }

    @Override
    public Optional<Language> findByCode(String code) {
        log.debug("Finding language by code: {}", code);
        return springLanguageRepository.findByCode(code)
                .map(languageMapper::toDomain);
    }

    @Override
    public List<Language> findAll() {
        log.debug("Finding all languages");
        return springLanguageRepository.findAll()
                .stream()
                .map(languageMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Language> findAllActive() {

        return springLanguageRepository.findByIsActiveTrue()
                .stream()
                .map(languageMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByCode(String code) {

        return springLanguageRepository.existsByCode(code);
    }

    @Override
    public void delete(Language language) {

        LanguageEntity entity = languageMapper.toEntity(language);
        springLanguageRepository.delete(entity);
    }

    @Override
    public void deleteById(UUID languageId) {

        springLanguageRepository.deleteById(languageId);
    }
}
