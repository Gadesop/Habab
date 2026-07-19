package com.example.sleekclient.setting;

/**
 * A numeric setting rendered as a draggable slider in the GUI.
 * Supports min/max bounds and configurable decimal precision.
 */
public class SliderSetting extends Setting<Double> {

    private final double minimum;
    private final double maximum;
    private final int decimals;

    public SliderSetting(String name, double defaultValue, double minimum, double maximum, int decimals) {
        super(name, defaultValue);
        this.minimum = minimum;
        this.maximum = maximum;
        this.decimals = decimals;
    }

    @Override
    public void setValue(Double value) {
        // Clamp to valid range
        super.setValue(Math.max(minimum, Math.min(maximum, value)));
    }

    public double getMinimum()  { return minimum; }
    public double getMaximum()  { return maximum; }
    public int getDecimals()    { return decimals; }

    /** Returns the current value as a percentage (0–1) of the slider range. */
    public float getProgress() {
        return (float) ((getValue() - minimum) / (maximum - minimum));
    }
}