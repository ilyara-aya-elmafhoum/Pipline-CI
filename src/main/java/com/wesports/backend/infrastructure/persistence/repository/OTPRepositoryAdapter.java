package com.wesports.backend.infrastructure.persistence.repository;

import com.wesports.backend.domain.model.OTP;
import com.wesports.backend.domain.repository.OTPRepository;
import com.wesports.backend.domain.valueobject.UserId;
import com.wesports.backend.infrastructure.persistence.jpa.SpringOTPRepository;
import com.wesports.backend.infrastructure.persistence.mapper.OTPMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class OTPRepositoryAdapter implements OTPRepository {

    private final SpringOTPRepository springOTPRepository;
    private final OTPMapper otpMapper;

    public OTPRepositoryAdapter(SpringOTPRepository springOTPRepository, OTPMapper otpMapper) {
        this.springOTPRepository = springOTPRepository;
        this.otpMapper = otpMapper;
    }

    @Override
    public OTP save(OTP otp) {
        var entity = otpMapper.toEntity(otp);
        var savedEntity = springOTPRepository.save(entity);
        return otpMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<OTP> findById(UUID otpId) {
        return springOTPRepository.findById(otpId)
                .map(otpMapper::toDomain);
    }

    @Override
    public Optional<OTP> findByUserIdAndType(UserId userId, String type) {
        return springOTPRepository.findByUserIdAndType(userId.getValue(), type)
                .map(otpMapper::toDomain);
    }

    @Override
    public Optional<OTP> findValidOTPByUserIdAndType(UserId userId, String type) {
        LocalDateTime validSince = LocalDateTime.now().minusMinutes(10); // 10 min validity
        return springOTPRepository.findValidOTPByUserIdAndType(userId.getValue(), type, validSince)
                .map(otpMapper::toDomain);
    }

    @Override
    public List<OTP> findByUserId(UserId userId) {
        return springOTPRepository.findByUserId(userId.getValue())
                .stream()
                .map(otpMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<OTP> findExpiredOTPs() {
        LocalDateTime expiredBefore = LocalDateTime.now().minusMinutes(10);
        return springOTPRepository.findExpiredOTPs(expiredBefore)
                .stream()
                .map(otpMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(OTP otp) {
        var entity = otpMapper.toEntity(otp);
        springOTPRepository.delete(entity);
    }

    @Override
    public void deleteById(UUID otpId) {
        springOTPRepository.deleteById(otpId);
    }

    @Override
    public void deleteByUserId(UserId userId) {
        springOTPRepository.deleteByUserId(userId.getValue());
    }

    @Override
    public void deleteExpiredOTPs() {
        LocalDateTime expiredBefore = LocalDateTime.now().minusMinutes(10);
        springOTPRepository.deleteExpiredOTPs(expiredBefore);
    }

    @Override
    public int deleteByExpiresAtBefore(LocalDateTime cutoff) {
        return springOTPRepository.deleteByCreatedAtBefore(cutoff);
    }

    @Override
    public int deleteByCreatedAtBefore(LocalDateTime cutoff) {
        return springOTPRepository.deleteByCreatedAtBefore(cutoff);
    }
}
