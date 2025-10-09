package com.wesports.backend.infrastructure.persistence.mapper;

import com.wesports.backend.domain.model.User;
import com.wesports.backend.domain.valueobject.Email;
import com.wesports.backend.domain.valueobject.Gender;
import com.wesports.backend.domain.valueobject.PhoneNumber;
import com.wesports.backend.domain.valueobject.RegistrationStep;
import com.wesports.backend.domain.valueobject.UserId;
import com.wesports.backend.domain.valueobject.UserRole;
import com.wesports.backend.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        entity.setId(user.getId().getValue());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setEmail(user.getEmail().getValue());
        
        // ✅ Handle phone number mapping
        if (user.getPhoneNumber() != null) {
            entity.setPhoneCountryCode(user.getPhoneNumber().getCountryCode());
            entity.setPhoneNumber(user.getPhoneNumber().getNumber());
        } else {
            entity.setPhoneCountryCode(null);
            entity.setPhoneNumber(null);
        }
        
        entity.setGender(mapGenderToEnum(user.getGender()));
        entity.setBirthday(user.getBirthday());
        entity.setLanguageId(user.getLanguageId());
        // Auth providers are now managed through UserAuthMethod entities, not in User entity
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setEmailVerified(user.isEmailVerified());
        entity.setRegistrationStep(mapRegistrationStepToEnum(user.getCurrentRegistrationStep()));
        entity.setUserRole(mapUserRoleToEnum(user.getUserRole()));
        entity.setNationality(user.getNationality());
        entity.setPlaceOfResidence(user.getLieuDeResidence());
        entity.setLanguages(mapLanguagesToArray(user.getLanguages()));
        entity.setRegistrationStep(mapRegistrationStepToEnum(user.getRegistrationStep()));

        return entity;
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        // ✅ Create PhoneNumber value object if phone data exists
        PhoneNumber phoneNumber = null;
        if (entity.getPhoneCountryCode() != null && entity.getPhoneNumber() != null) {
            phoneNumber = new PhoneNumber(entity.getPhoneCountryCode(), entity.getPhoneNumber());
        }

        User user = new User(
            UserId.of(entity.getId()),
            entity.getFirstName(),
            entity.getLastName(),
            Email.of(entity.getEmail()),
            phoneNumber, // ✅ Added phone number parameter
            mapEnumToGender(entity.getGender()),
            entity.getBirthday(),
            entity.getLanguageId(),
            entity.getCreatedAt(),
            mapEnumToRegistrationStep(entity.getRegistrationStep()) // ✅ Added registration step
        );
        
        // Set authentication fields
        user.setUpdatedAt(entity.getUpdatedAt());
        user.setPasswordHash(entity.getPasswordHash());
        user.setEmailVerified(entity.getEmailVerified() != null ? entity.getEmailVerified() : false);
        
        // Set role
        user.setUserRole(mapEnumToUserRole(entity.getUserRole()));
        
        // Set additional profile fields
        user.setNationality(entity.getNationality());
        user.setLieuDeResidence(entity.getPlaceOfResidence());
        user.setLanguages(mapArrayToLanguages(entity.getLanguages()));
        
        return user;
    }

    private UserEntity.GenderEnum mapGenderToEnum(Gender gender) {
        if (gender == null) {
            return null;
        }
        
        switch (gender) {
            case MALE: return UserEntity.GenderEnum.MALE;
            case FEMALE: return UserEntity.GenderEnum.FEMALE;
            default: throw new IllegalArgumentException("Unknown gender: " + gender);
        }
    }

    private Gender mapEnumToGender(UserEntity.GenderEnum genderEnum) {
        if (genderEnum == null) {
            return null;
        }
        
        switch (genderEnum) {
            case MALE: return Gender.MALE;
            case FEMALE: return Gender.FEMALE;
            default: throw new IllegalArgumentException("Unknown gender enum: " + genderEnum);
        }
    }
    
    private String[] mapLanguagesToArray(java.util.List<String> languages) {
        if (languages == null || languages.isEmpty()) {
            return null;
        }
        return languages.toArray(new String[0]);
    }
    
    private java.util.List<String> mapArrayToLanguages(String[] languagesArray) {
        if (languagesArray == null || languagesArray.length == 0) {
            return null;
        }
        return java.util.Arrays.asList(languagesArray);
    }
    
    private UserEntity.RegistrationStepEnum mapRegistrationStepToEnum(RegistrationStep step) {
        if (step == null) {
            return UserEntity.RegistrationStepEnum.EMAIL_VERIFICATION;
        }
        
        return switch (step) {
            case EMAIL_VERIFICATION -> UserEntity.RegistrationStepEnum.EMAIL_VERIFICATION;
            case PASSWORD_SETUP -> UserEntity.RegistrationStepEnum.PASSWORD_SETUP;
            case ROLE_SELECTION -> UserEntity.RegistrationStepEnum.ROLE_SELECTION;
            case PROFILE_FORM -> UserEntity.RegistrationStepEnum.PROFILE_FORM;
            case ONBOARDING -> UserEntity.RegistrationStepEnum.ONBOARDING;
            case COMPLETED -> UserEntity.RegistrationStepEnum.COMPLETED;
        };
    }
    
    private RegistrationStep mapEnumToRegistrationStep(UserEntity.RegistrationStepEnum stepEnum) {
        if (stepEnum == null) {
            return RegistrationStep.EMAIL_VERIFICATION;
        }
        
        return switch (stepEnum) {
            case EMAIL_VERIFICATION -> RegistrationStep.EMAIL_VERIFICATION;
            case PASSWORD_SETUP -> RegistrationStep.PASSWORD_SETUP;
            case ROLE_SELECTION -> RegistrationStep.ROLE_SELECTION;
            case PROFILE_FORM -> RegistrationStep.PROFILE_FORM;
            case ONBOARDING -> RegistrationStep.ONBOARDING;
            case COMPLETED -> RegistrationStep.COMPLETED;
        };
    }
    
    private UserEntity.UserRoleEnum mapUserRoleToEnum(UserRole userRole) {
        if (userRole == null) {
            return null;
        }
        
        return switch (userRole) {
            case PLAYER -> UserEntity.UserRoleEnum.PLAYER;
            case COACH -> UserEntity.UserRoleEnum.COACH;
            case CLUB -> UserEntity.UserRoleEnum.CLUB_REPRESENTATIVE;
            case AGENT -> UserEntity.UserRoleEnum.AGENT;
            case REPRESENTANT -> UserEntity.UserRoleEnum.CLUB_REPRESENTATIVE;
        };
    }
    
    private UserRole mapEnumToUserRole(UserEntity.UserRoleEnum userRoleEnum) {
        if (userRoleEnum == null) {
            return null;
        }
        
        return switch (userRoleEnum) {
            case PLAYER -> UserRole.PLAYER;
            case COACH -> UserRole.COACH;
            case CLUB_REPRESENTATIVE -> UserRole.CLUB;
            case AGENT -> UserRole.AGENT;
            case PARENT -> UserRole.REPRESENTANT; // Map parent to representant for now
            case SCOUT -> UserRole.AGENT; // Map scout to agent for now
        };
    }
}
