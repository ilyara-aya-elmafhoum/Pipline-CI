package com.wesports.backend.infrastructure.persistence.mapper;

import com.wesports.backend.domain.model.Language;
import com.wesports.backend.infrastructure.persistence.entity.LanguageEntity;
import org.springframework.stereotype.Component;

@Component
public class LanguageMapper {

    public LanguageEntity toEntity(Language language) {
        if (language == null) {
            return null;
        }

        return new LanguageEntity(
            language.getId(),
            language.getName(),
            language.getCode(),
            language.isActive()
        );
    }

    public Language toDomain(LanguageEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Language(
            entity.getId(),
            entity.getName(),
            entity.getCode(),
            entity.isActive()
        );
    }
}
