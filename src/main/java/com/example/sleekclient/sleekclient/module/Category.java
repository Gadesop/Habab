package com.example.sleekclient.module;

/**
 * Enum representing the three GUI category tabs.
 * Each module belongs to exactly one category.
 */
public enum Category {
    COMBAT("Combat"),
    PLAYER("Player"),
    VISUALS("Visuals");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}