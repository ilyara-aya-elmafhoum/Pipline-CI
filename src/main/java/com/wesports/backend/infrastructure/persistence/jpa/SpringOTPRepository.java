package com.wesports.backend.infrastructure.persistence.jpa;

import com.wesports.backend.infrastructure.persistence.entity.OTPEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringOTPRepository extends JpaRepository<OTPEntity, UUID> {
    
    Optional<OTPEntity> findByUserIdAndType(UUID userId, String type);
    
    List<OTPEntity> findByUserId(UUID userId);
    
    @Query("SELECT o FROM OTPEntity o WHERE o.userId = :userId AND o.type = :type AND o.createdAt > :validSince AND o.attempts < 3")
    Optional<OTPEntity> findValidOTPByUserIdAndType(@Param("userId") UUID userId, 
                                                    @Param("type") String type, 
                                                    @Param("validSince") LocalDateTime validSince);
    
    @Query("SELECT o FROM OTPEntity o WHERE o.createdAt < :expiredBefore")
    List<OTPEntity> findExpiredOTPs(@Param("expiredBefore") LocalDateTime expiredBefore);
    
    @Modifying
    @Query("DELETE FROM OTPEntity o WHERE o.userId = :userId")
    void deleteByUserId(@Param("userId") UUID userId);
    
    @Modifying
    @Query("DELETE FROM OTPEntity o WHERE o.createdAt < :expiredBefore")
    void deleteExpiredOTPs(@Param("expiredBefore") LocalDateTime expiredBefore);
    
    @Modifying
    @Query("DELETE FROM OTPEntity o WHERE o.createdAt < :cutoff")
    int deleteByCreatedAtBefore(@Param("cutoff") LocalDateTime cutoff);
}
