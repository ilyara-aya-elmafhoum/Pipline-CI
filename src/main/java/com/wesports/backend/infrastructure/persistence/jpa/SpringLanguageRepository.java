package com.wesports.backend.infrastructure.persistence.jpa;

import com.wesports.backend.infrastructure.persistence.entity.LanguageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringLanguageRepository extends JpaRepository<LanguageEntity, UUID> {
    
    Optional<LanguageEntity> findByCode(String code);
    
    List<LanguageEntity> findByIsActiveTrue();
    
    boolean existsByCode(String code);
}
