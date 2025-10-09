package com.wesports.backend.infrastructure.persistence.repository;

import com.wesports.backend.domain.model.Sport;
import com.wesports.backend.domain.repository.SportRepository;
import com.wesports.backend.domain.valueobject.SportId;
import com.wesports.backend.infrastructure.persistence.entity.SportEntity;
import com.wesports.backend.infrastructure.persistence.jpa.SpringSportRepository;
import com.wesports.backend.infrastructure.persistence.mapper.SportMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of SportRepository that adapts JPA repository to domain repository
 * Part of the infrastructure layer in hexagonal architecture
 */
@Repository
public class SportRepositoryAdapter implements SportRepository {
    
    private final SpringSportRepository springSportRepository;
    private final SportMapper sportMapper;
    
    @Autowired
    public SportRepositoryAdapter(SpringSportRepository springSportRepository, SportMapper sportMapper) {
        this.springSportRepository = springSportRepository;
        this.sportMapper = sportMapper;
    }
    
    @Override
    public Sport save(Sport sport) {
        SportEntity entity = sportMapper.toEntity(sport);
        SportEntity savedEntity = springSportRepository.save(entity);
        return sportMapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Sport> findById(SportId sportId) {
        Optional<SportEntity> entityOpt = springSportRepository.findById(sportId.getValue());
        return entityOpt.map(sportMapper::toDomain);
    }
    
    @Override
    public Optional<Sport> findByCode(String code) {
        Optional<SportEntity> entityOpt = springSportRepository.findByCode(code);
        return entityOpt.map(sportMapper::toDomain);
    }
    
    @Override
    public Optional<Sport> findByName(String name) {
        Optional<SportEntity> entityOpt = springSportRepository.findByNameIgnoreCase(name);
        return entityOpt.map(sportMapper::toDomain);
    }
    
    @Override
    public List<Sport> findAllActive() {
        List<SportEntity> entities = springSportRepository.findAllActive();
        return entities.stream()
                .map(sportMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Sport> findAll() {
        List<SportEntity> entities = springSportRepository.findAll();
        return entities.stream()
                .map(sportMapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByCode(String code) {
        return springSportRepository.existsByCode(code);
    }
    
    @Override
    public boolean existsByName(String name) {
        return springSportRepository.existsByNameIgnoreCase(name);
    }
    
    @Override
    public void delete(Sport sport) {
        SportEntity entity = sportMapper.toEntity(sport);
        springSportRepository.delete(entity);
    }
    
    @Override
    public void deleteById(SportId sportId) {
        springSportRepository.deleteById(sportId.getValue());
    }
}
