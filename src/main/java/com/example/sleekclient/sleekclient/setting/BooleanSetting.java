package com.example.sleekclient.setting;

/**
 * A boolean (on/off) setting rendered as a toggle switch in the GUI.
 */
public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String name, boolean defaultValue) {
        super(name, defaultValue);
    }
}