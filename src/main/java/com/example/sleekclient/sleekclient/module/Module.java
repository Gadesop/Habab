package com.example.sleekclient.module;

import com.example.sleekclient.setting.Setting;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for all modules.
 * Provides enable/disable state, settings list, category assignment,
 * and lifecycle hooks (onEnable, onDisable, onTick, onRender3D).
 */
public abstract class Module {

    protected final Minecraft mc = Minecraft.getMinecraft();

    private final String name;
    private final String description;
    private final Category category;

    private boolean enabled;
    private final List<Setting<?>> settings = new ArrayList<>();

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = false;
    }

    /** Toggle the module on/off. */
    public void toggle() {
        setEnabled(!enabled);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    // --- Lifecycle hooks (override in subclasses as needed) ---

    public void onEnable()  { /* override */ }
    public void onDisable() { /* override */ }

    /** Called every client tick (20 TPS) while enabled. */
    public void onTick()    { /* override */ }

    /** Called during RenderWorldLastEvent while enabled. */
    public void onRender3D(float partialTicks) { /* override */ }

    // --- Settings management ---

    protected void addSetting(Setting<?> setting) {
        settings.add(setting);
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    // --- Getters ---

    public String getName()        { return name; }
    public String getDescription() { return description; }
    public Category getCategory()  { return category; }
    public boolean isEnabled()     { return enabled; }
}