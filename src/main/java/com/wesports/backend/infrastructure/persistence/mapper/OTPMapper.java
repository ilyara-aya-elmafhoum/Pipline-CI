package com.wesports.backend.infrastructure.persistence.mapper;

import com.wesports.backend.domain.model.OTP;
import com.wesports.backend.domain.valueobject.UserId;
import com.wesports.backend.infrastructure.persistence.entity.OTPEntity;
import org.springframework.stereotype.Component;

@Component
public class OTPMapper {

    public OTPEntity toEntity(OTP otp) {
        if (otp == null) {
            return null;
        }

        return new OTPEntity(
            otp.getId(),
            otp.getUserId().getValue(),
            otp.getOtpCode(),
            otp.getCreatedAt(),
            otp.getAttempts(),
            otp.getType(),
            otp.getLanguageCode()
        );
    }

    public OTP toDomain(OTPEntity entity) {
        if (entity == null) {
            return null;
        }

        return new OTP(
            entity.getId(),
            UserId.of(entity.getUserId()),
            entity.getOtpCode(),
            entity.getCreatedAt(),
            entity.getAttempts(),
            entity.getType(),
            entity.getLanguageCode()
        );
    }
}
