package com.wesports.backend.domain.valueobject;

public enum Position {
    // Goalkeeper (FIFA nomenclature)
    GK("Goalkeeper"),
    
    // Defenders
    CB("Center Back"),
    LB("Left Back"),
    RB("Right Back"),
    LWB("Left Wing Back"),
    RWB("Right Wing Back"),
    
    // Midfielders
    CDM("Central Defensive Midfielder"),
    CM("Central Midfielder"),
    CAM("Central Attacking Midfielder"),
    LM("Left Midfielder"),
    RM("Right Midfielder"),
    
    // Forwards/Attackers
    LW("Left Winger"),
    RW("Right Winger"),
    ST("Striker"),
    CF("Center Forward"),
    LF("Left Forward"),
    RF("Right Forward");
    
    private final String displayName;
    
    Position(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static Position fromString(String position) {
        if (position == null || position.trim().isEmpty()) {
            throw new IllegalArgumentException("Position cannot be null or empty");
        }
        
        try {
            return Position.valueOf(position.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid position value: " + position + 
                ". Valid values are: " + String.join(", ", getPositionCodes()));
        }
    }
    
    public static String[] getPositionCodes() {
        Position[] positions = Position.values();
        String[] codes = new String[positions.length];
        for (int i = 0; i < positions.length; i++) {
            codes[i] = positions[i].name();
        }
        return codes;
    }
    
    @Override
    public String toString() {
        return this.name() + " (" + this.displayName + ")";
    }
}
