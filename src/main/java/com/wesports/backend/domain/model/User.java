package com.wesports.backend.domain.model;

import com.wesports.backend.domain.valueobject.Email;
import com.wesports.backend.domain.valueobject.Gender;

import com.wesports.backend.domain.valueobject.UserId;
import com.wesports.backend.domain.valueobject.UserRole;
import com.wesports.backend.domain.valueobject.PhoneNumber;
import com.wesports.backend.domain.valueobject.RegistrationStep;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.util.List;

public class User {
    private UserId id;
    private String firstName;
    private String lastName;
    private Email email;
    private PhoneNumber phoneNumber;  // ✅ Added phone number
    private UserRole userRole;  // ✅ Added for role selection
    private RegistrationStep registrationStep; // ✅ Track registration progress
    private Gender gender;
    private LocalDate birthday;
    private String nationality;  // ✅ Added for profile form
    private String lieuDeResidence;  // ✅ Added for profile form
    private List<String> languages;  // ✅ Added for profile form
    private UUID languageId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String passwordHash;
    private boolean emailVerified;
    
    // Protected constructor for inheritance
    protected User() {
        this.id = UserId.generate();
        this.createdAt = LocalDateTime.now();
    }

    // Constructor for creating new users
    public User(String firstName, String lastName, Email email, Gender gender, LocalDate birthday) {
        this();
        this.firstName = validateFirstName(firstName);
        this.lastName = validateLastName(lastName);
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.gender = gender;
        this.birthday = birthday;
        this.registrationStep = RegistrationStep.EMAIL_VERIFICATION; // Start with email verification
    }

    // Constructor for loading from database
    public User(UserId id, String firstName, String lastName, Email email, PhoneNumber phoneNumber,
                Gender gender, LocalDate birthday, UUID languageId, 
                LocalDateTime createdAt, RegistrationStep registrationStep) {
        this.id = Objects.requireNonNull(id, "User ID cannot be null");
        this.firstName = validateFirstName(firstName);
        this.lastName = validateLastName(lastName);
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.phoneNumber = phoneNumber; // Can be null
        this.gender = gender;
        this.birthday = birthday;
        this.languageId = languageId;
        this.createdAt = Objects.requireNonNull(createdAt, "Created at cannot be null");
        this.registrationStep = registrationStep != null ? registrationStep : RegistrationStep.EMAIL_VERIFICATION;
    }

    // Business logic methods
    public boolean canLogin() {
        return email != null;
        // Note: Auth method validation is now handled by UserAuthMethod entity
    }


    public boolean isProfileComplete() {
        return firstName != null && lastName != null && email != null && gender != null && phoneNumber != null;
    }

    public void updateProfile(String firstName, String lastName, Gender gender, LocalDate birthday) {
        this.firstName = validateFirstName(firstName);
        this.lastName = validateLastName(lastName);
        this.gender = gender;
        this.birthday = birthday;
        this.updatedAt = LocalDateTime.now();
    }
    
    // ✅ New phone number methods
    public void updatePhoneNumber(String countryCode, String number) {
        this.phoneNumber = new PhoneNumber(countryCode, number);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void removePhoneNumber() {
        this.phoneNumber = null;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean hasPhoneNumber() {
        return phoneNumber != null;
    }

    public void setPreferredLanguage(UUID languageId) {
        this.languageId = languageId;
    }

    // ✅ Registration step business methods
    public void advanceRegistrationStep() {
        this.registrationStep = this.registrationStep.getNextStep();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setRegistrationStep(RegistrationStep step) {
        this.registrationStep = Objects.requireNonNull(step, "Registration step cannot be null");
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isRegistrationCompleted() {
        return registrationStep != null && registrationStep.isCompleted();
    }
    
    public RegistrationStep getCurrentRegistrationStep() {
        return registrationStep;
    }

    // Validation methods
    private String validateFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        return firstName.trim();
    }

    private String validateLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        return lastName.trim();
    }

    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return firstName != null ? firstName : lastName;
    }

    // Getters
    public UserId getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Email getEmail() { return email; }
    public PhoneNumber getPhoneNumber() { return phoneNumber; }
    public RegistrationStep getRegistrationStep() { return registrationStep; }
    public Gender getGender() { return gender; }
    public LocalDate getBirthday() { return birthday; }
    public UUID getLanguageId() { return languageId; }
    // Note: Auth providers are now managed through UserAuthMethod entities
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isEmailVerified() { return emailVerified; }
    
    // Setters for authentication fields
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // ✅ Basic profile setters
    public void setFirstName(String firstName) { 
        this.firstName = validateFirstName(firstName); 
    }
    public void setLastName(String lastName) { 
        this.lastName = validateLastName(lastName); 
    }
    public void setGender(Gender gender) { this.gender = gender; }
    public void setBirthday(LocalDate birthday) { this.birthday = birthday; }

    // ✅ New getters and setters for added fields
    public UserRole getUserRole() { return userRole; }
    public void setUserRole(UserRole userRole) { this.userRole = userRole; }
    
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    
    public String getLieuDeResidence() { return lieuDeResidence; }
    public void setLieuDeResidence(String lieuDeResidence) { this.lieuDeResidence = lieuDeResidence; }
    
    public List<String> getLanguages() { return languages; }
    public void setLanguages(List<String> languages) { this.languages = languages; }


    public boolean isPlayer() {
        return userRole == UserRole.PLAYER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email=" + email +
                ", gender=" + gender +
                ", createdAt=" + createdAt +
                '}';
    }
}
