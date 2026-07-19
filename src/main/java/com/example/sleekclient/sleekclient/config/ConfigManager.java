package com.example.sleekclient.config;

import com.example.sleekclient.SleekClient;
import com.example.sleekclient.module.Module;
import com.example.sleekclient.setting.BooleanSetting;
import com.example.sleekclient.setting.Setting;
import com.example.sleekclient.setting.SliderSetting;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Saves and loads module enabled-states and setting values
 * to/from a JSON file in the Minecraft config directory.
 */
public class ConfigManager {

    private static final File CONFIG_DIR  = new File(Minecraft.getMinecraft().mcDataDir, "config");
    private static final File CONFIG_FILE = new File(CONFIG_DIR, "sleekclient.json");

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /** Serialize all modules and settings to disk. */
    public void save() {
        JsonObject root = new JsonObject();
        JsonArray modulesArray = new JsonArray();

        for (Module module : SleekClient.getInstance().getModuleManager().getModules()) {
            JsonObject moduleObj = new JsonObject();
            moduleObj.addProperty("name", module.getName());
            moduleObj.addProperty("enabled", module.isEnabled());

            JsonObject settingsObj = new JsonObject();
            for (Setting<?> setting : module.getSettings()) {
                if (setting instanceof BooleanSetting) {
                    settingsObj.addProperty(setting.getName(), ((BooleanSetting) setting).getValue());
                } else if (setting instanceof SliderSetting) {
                    settingsObj.addProperty(setting.getName(), ((SliderSetting) setting).getValue());
                }
            }
            moduleObj.add("settings", settingsObj);
            modulesArray.add(moduleObj);
        }

        root.add("modules", modulesArray);

        try {
            CONFIG_DIR.mkdirs();
            Files.write(CONFIG_FILE.toPath(), gson.toJson(root).getBytes());
        } catch (IOException e) {
            System.err.println("[SleekClient] Failed to save config: " + e.getMessage());
        }
    }

    /** Load module states and settings from disk. */
    public void load() {
        if (!CONFIG_FILE.exists()) return;

        try {
            String content = new String(Files.readAllBytes(CONFIG_FILE.toPath()));
            JsonObject root = gson.fromJson(content, JsonObject.class);
            if (root == null || !root.has("modules")) return;

            JsonArray modulesArray = root.getAsJsonArray("modules");
            for (JsonElement element : modulesArray) {
                JsonObject moduleObj = element.getAsJsonObject();
                String name = moduleObj.get("name").getAsString();
                Module module = SleekClient.getInstance().getModuleManager().getModuleByName(name);
                if (module == null) continue;

                // Restore enabled state
                if (moduleObj.has("enabled")) {
                    module.setEnabled(moduleObj.get("enabled").getAsBoolean());
                }

                // Restore settings
                if (moduleObj.has("settings")) {
                    JsonObject settingsObj = moduleObj.getAsJsonObject("settings");
                    for (Setting<?> setting : module.getSettings()) {
                        if (!settingsObj.has(setting.getName())) continue;
                        JsonElement val = settingsObj.get(setting.getName());
                        if (setting instanceof BooleanSetting) {
                            ((BooleanSetting) setting).setValue(val.getAsBoolean());
                        } else if (setting instanceof SliderSetting) {
                            ((SliderSetting) setting).setValue(val.getAsDouble());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[SleekClient] Failed to load config: " + e.getMessage());
        }
    }
}