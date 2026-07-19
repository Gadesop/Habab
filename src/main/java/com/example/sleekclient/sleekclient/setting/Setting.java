package com.example.sleekclient.setting;

/**
 * Generic abstract base for settings.
 * Each setting has a name and a typed value.
 */
public abstract class Setting<T> {

    private final String name;
    private T value;

    public Setting(String name, T defaultValue) {
        this.name = name;
        this.value = defaultValue;
    }

    public String getName()  { return name; }
    public T getValue()      { return value; }
    public void setValue(T value) { this.value = value; }
}