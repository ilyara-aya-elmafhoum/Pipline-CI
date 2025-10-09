package com.wesports.backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserEntity {
    
    @Id
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    // ✅ Added phone fields
    @Column(name = "phone_country_code")
    private String phoneCountryCode;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private GenderEnum gender;
    
    @Column(name = "birthday")
    private LocalDate birthday;
    
    @Column(name = "language_id", columnDefinition = "UUID")
    private UUID languageId;
    
    // Legacy auth_providers field removed - now using UserAuthMethod entities
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "password_hash")
    private String passwordHash;
    
    @Column(name = "email_verified")
    private Boolean emailVerified = false;
    
    // ✅ Registration step field
    @Enumerated(EnumType.STRING)
    @Column(name = "registration_step")
    private RegistrationStepEnum registrationStep;
    
    // ✅ User role field
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRoleEnum userRole;
    
    @Column(name = "nationality")
    private String nationality;
    
    @Column(name = "place_of_residence") 
    private String placeOfResidence;

    @Column(name = "languages", columnDefinition = "text[]")
    private String[] languages;

    // Default constructor
    public UserEntity() {}

    // Constructor
    public UserEntity(UUID id, String firstName, String lastName, String email, 
                     String phoneCountryCode, String phoneNumber, // ✅ Added phone parameters
                     GenderEnum gender, LocalDate birthday, UUID languageId, 
                     LocalDateTime createdAt, LocalDateTime updatedAt,
                     String passwordHash, Boolean emailVerified, UserRoleEnum userRole, String nationality, 
                     String placeOfResidence, String[] languages) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneCountryCode = phoneCountryCode; // ✅ Added
        this.phoneNumber = phoneNumber; // ✅ Added
        this.gender = gender;
        this.birthday = birthday;
        this.languageId = languageId;
        // Auth providers are now managed through UserAuthMethod entities
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.passwordHash = passwordHash;
        this.emailVerified = emailVerified;
        this.userRole = userRole;
        this.nationality = nationality;
        this.placeOfResidence = placeOfResidence;
        this.languages = languages;
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // ✅ Phone number getters and setters
    public String getPhoneCountryCode() { return phoneCountryCode; }
    public void setPhoneCountryCode(String phoneCountryCode) { this.phoneCountryCode = phoneCountryCode; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public GenderEnum getGender() { return gender; }
    public void setGender(GenderEnum gender) { this.gender = gender; }

    public LocalDate getBirthday() { return birthday; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }

    public UUID getLanguageId() { return languageId; }
    public void setLanguageId(UUID languageId) { this.languageId = languageId; }

    // Legacy auth providers methods removed - now using UserAuthMethod entities

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }
    
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    
    public String getPlaceOfResidence() { return placeOfResidence; }
    public void setPlaceOfResidence(String placeOfResidence) { this.placeOfResidence = placeOfResidence; }
    
    public String[] getLanguages() { return languages; }
    public void setLanguages(String[] languages) { this.languages = languages; }

    // ✅ Registration step getter and setter
    public RegistrationStepEnum getRegistrationStep() { return registrationStep; }
    public void setRegistrationStep(RegistrationStepEnum registrationStep) { this.registrationStep = registrationStep; }
    
    // ✅ User role getter and setter
    public UserRoleEnum getUserRole() { return userRole; }
    public void setUserRole(UserRoleEnum userRole) { this.userRole = userRole; }

    // Enum for Gender
    public enum GenderEnum {
        MALE, FEMALE
    }

    // Enum for Registration Step
    public enum RegistrationStepEnum {
        EMAIL_VERIFICATION, PASSWORD_SETUP, ROLE_SELECTION, PROFILE_FORM, ONBOARDING, COMPLETED
    }
    
    // Enum for User Role
    public enum UserRoleEnum {
        PLAYER, COACH, PARENT, SCOUT, CLUB_REPRESENTATIVE, AGENT
    }
}
