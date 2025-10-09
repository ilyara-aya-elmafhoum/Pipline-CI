package com.wesports.backend.domain.valueobject;

public enum Category {
    U13("Under 13"),
    U14("Under 14"),
    U15("Under 15"),
    U16("Under 16"),
    U17("Under 17"),
    U18("Under 18"),
    U19("Under 19"),
    SENIOR("Senior");
    
    private final String displayName;
    
    Category(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static Category fromString(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        
        try {
            return Category.valueOf(category.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid category value: " + category + 
                ". Valid values are: " + String.join(", ", getCategoryCodes()));
        }
    }
    
    public static String[] getCategoryCodes() {
        Category[] categories = Category.values();
        String[] codes = new String[categories.length];
        for (int i = 0; i < categories.length; i++) {
            codes[i] = categories[i].name();
        }
        return codes;
    }
    
    public boolean isYouthCategory() {
        return this != SENIOR;
    }
    
    public boolean isAdultCategory() {
        return this == SENIOR;
    }
    
    @Override
    public String toString() {
        return this.name() + " (" + this.displayName + ")";
    }
}
