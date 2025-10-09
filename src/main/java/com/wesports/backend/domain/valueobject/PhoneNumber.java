package com.wesports.backend.domain.valueobject;

import java.util.Objects;

public class PhoneNumber {
    private final String countryCode;
    private final String number;
    
    public PhoneNumber(String countryCode, String number) {
        this.countryCode = validateCountryCode(countryCode);
        this.number = validateNumber(number);
    }
    
    private String validateCountryCode(String countryCode) {
        if (countryCode == null || countryCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Country code cannot be null or empty");
        }
        
        String cleaned = countryCode.trim();
        if (!cleaned.startsWith("+")) {
            cleaned = "+" + cleaned;
        }
        
        // Basic validation for country codes (1-4 digits after +)
        if (!cleaned.matches("\\+\\d{1,4}")) {
            throw new IllegalArgumentException("Invalid country code format. Expected +XXX format");
        }
        
        return cleaned;
    }
    
    private String validateNumber(String number) {
        if (number == null || number.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or empty");
        }
        
        // Remove spaces, dashes, parentheses
        String cleaned = number.replaceAll("[\\s\\-\\(\\)]", "");
        
        // Should contain only digits
        if (!cleaned.matches("\\d{6,15}")) {
            throw new IllegalArgumentException("Phone number must contain 6-15 digits only");
        }
        
        return cleaned;
    }
    
    public String getFullNumber() {
        return countryCode + number;
    }
    
    public String getCountryCode() { 
        return countryCode; 
    }
    
    public String getNumber() { 
        return number; 
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneNumber that = (PhoneNumber) o;
        return Objects.equals(countryCode, that.countryCode) && 
               Objects.equals(number, that.number);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(countryCode, number);
    }
    
    @Override
    public String toString() {
        return getFullNumber();
    }
}
