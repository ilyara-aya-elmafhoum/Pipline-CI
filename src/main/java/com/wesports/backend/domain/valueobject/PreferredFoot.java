package com.wesports.backend.domain.valueobject;

public enum PreferredFoot {
    LEFT("Left Foot"),
    RIGHT("Right Foot"),
    BOTH("Both Feet");
    
    private final String displayName;
    
    PreferredFoot(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static PreferredFoot fromString(String foot) {
        if (foot == null || foot.trim().isEmpty()) {
            throw new IllegalArgumentException("Preferred foot cannot be null or empty");
        }
        
        try {
            return PreferredFoot.valueOf(foot.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid preferred foot value: " + foot + 
                ". Valid values are: LEFT, RIGHT, BOTH");
        }
    }
}
