package com.example.sleekclient;

import com.example.sleekclient.config.ConfigManager;
import com.example.sleekclient.gui.SleekGui;
import com.example.sleekclient.module.ModuleManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

/**
 * Main mod class. Entry point for Forge.
 * Registers event handlers, initializes module/config systems,
 * and handles the GUI keybind (Right Shift).
 */
@Mod(modid = SleekClient.MOD_ID, name = SleekClient.MOD_NAME, version = SleekClient.MOD_VERSION)
public class SleekClient {

    public static final String MOD_ID = "sleekclient";
    public static final String MOD_NAME = "SleekClient";
    public static final String MOD_VERSION = "1.0.0";

    private static SleekClient instance;

    private ModuleManager moduleManager;
    private ConfigManager configManager;

    /** Tracks previous key state for edge detection on the GUI keybind. */
    private boolean guiKeyWasDown = false;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        instance = this;

        // Initialize module system first so config can load into modules
        moduleManager = new ModuleManager();

        // Load saved config (module states + setting values)
        configManager = new ConfigManager();
        configManager.load();

        // Register event handlers on the Forge bus
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(moduleManager);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getMinecraft();

        // Edge-detect Right Shift to open/close GUI
        boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
        if (keyDown && !guiKeyWasDown) {
            if (mc.currentScreen == null) {
                // Open GUI
                mc.displayGuiScreen(new SleekGui());
            }
        }
        guiKeyWasDown = keyDown;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public static SleekClient getInstance() {
        return instance;
    }
}