package com.wesports.backend.infrastructure.persistence.jpa;

import com.wesports.backend.infrastructure.persistence.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringPlayerRepository extends JpaRepository<PlayerEntity, UUID> {
    
    List<PlayerEntity> findByPostId(UUID postId);
    
    @Query("SELECT p FROM PlayerEntity p WHERE p.height BETWEEN :minHeight AND :maxHeight")
    List<PlayerEntity> findByHeightRange(@Param("minHeight") Float minHeight, @Param("maxHeight") Float maxHeight);
    
    @Query("SELECT p FROM PlayerEntity p WHERE p.weight BETWEEN :minWeight AND :maxWeight")
    List<PlayerEntity> findByWeightRange(@Param("minWeight") Float minWeight, @Param("maxWeight") Float maxWeight);
}
