package com.wesports.backend.infrastructure.persistence.jpa;

import com.wesports.backend.infrastructure.persistence.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringPostRepository extends JpaRepository<PostEntity, UUID> {
    
    Optional<PostEntity> findByCode(String code);
    
    boolean existsByCode(String code);
}
