package com.example.sleekclient.module;

import com.example.sleekclient.module.combat.LeftClicker;
import com.example.sleekclient.module.combat.RightClicker;
import com.example.sleekclient.module.player.BridgingAssist;
import com.example.sleekclient.module.visuals.HitboxESP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages all module instances.
 * Dispatches Forge tick and render events to enabled modules.
 */
public class ModuleManager {

    private final List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        // Combat
        register(new LeftClicker());
        register(new RightClicker());
        // Player
        register(new BridgingAssist());
        // Visuals
        register(new HitboxESP());
    }

    private void register(Module module) {
        modules.add(module);
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getModulesByCategory(Category category) {
        return modules.stream()
                .filter(m -> m.getCategory() == category)
                .collect(Collectors.toList());
    }

    public Module getModuleByName(String name) {
        return modules.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    // --- Event dispatch ---

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onTick();
            }
        }
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onRender3D(event.getPartialTicks());
            }
        }
    }
}