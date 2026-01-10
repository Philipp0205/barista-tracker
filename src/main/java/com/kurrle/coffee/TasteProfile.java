package com.kurrle.coffee;

/**
 * Represents the 8 positions on the espresso dial-in compass.
 * Each position corresponds to a specific taste characteristic
 * and has associated recommendations for adjustment.
 */
public enum TasteProfile {
    SOUR("Too Sour/Acidic", "Increase yield", true, false, false, false),
    MUDDY_SOUR("Muddy + Sour", "Increase yield and grind coarser", true, false, true, false),
    MUDDY("High Strength/Muddy", "Grind coarser", false, false, true, false),
    MUDDY_BITTER("Muddy + Bitter", "Decrease yield and grind coarser", false, true, true, false),
    BITTER("Too Bitter", "Decrease yield", false, true, false, false),
    WATERY_BITTER("Watery + Bitter", "Decrease yield and grind finer", false, true, false, true),
    WATERY("Low Strength/Watery", "Grind finer", false, false, false, true),
    WATERY_SOUR("Watery + Sour", "Increase yield and grind finer", true, false, false, true),
    BALANCED("Balanced/Perfect", "No adjustments needed!", false, false, false, false);

    private final String displayName;
    private final String recommendation;
    private final boolean sour;
    private final boolean bitter;
    private final boolean muddy;
    private final boolean watery;

    TasteProfile(String displayName, String recommendation, boolean sour, boolean bitter, boolean muddy, boolean watery) {
        this.displayName = displayName;
        this.recommendation = recommendation;
        this.sour = sour;
        this.bitter = bitter;
        this.muddy = muddy;
        this.watery = watery;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public boolean isSour() {
        return sour;
    }

    public boolean isBitter() {
        return bitter;
    }

    public boolean isMuddy() {
        return muddy;
    }

    public boolean isWatery() {
        return watery;
    }

    /**
     * Returns true if grind should be finer
     */
    public boolean shouldGrindFiner() {
        return watery;
    }

    /**
     * Returns true if grind should be coarser
     */
    public boolean shouldGrindCoarser() {
        return muddy;
    }

    /**
     * Returns true if yield should increase
     */
    public boolean shouldIncreaseYield() {
        return sour;
    }

    /**
     * Returns true if yield should decrease
     */
    public boolean shouldDecreaseYield() {
        return bitter;
    }
}
