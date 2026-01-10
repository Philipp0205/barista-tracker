package com.kurrle.coffee;

public enum RoastLevel {
    LIGHT("Light"),
    MEDIUM_LIGHT("Medium-Light"),
    MEDIUM("Medium"),
    MEDIUM_DARK("Medium-Dark"),
    DARK("Dark");

    private final String displayName;

    RoastLevel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
