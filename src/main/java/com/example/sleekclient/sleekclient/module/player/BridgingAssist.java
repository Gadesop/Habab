package com.example.sleekclient.module.player;

import com.example.sleekclient.module.Category;
import com.example.sleekclient.module.Module;
import com.example.sleekclient.setting.SliderSetting;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.BlockPos;

/**
 * BridgingAssist — automatically holds sneak when the player
 * approaches a block edge to prevent falling off while bridging.
 * Includes a configurable delay between edge detection and sneak activation.
 */
public class BridgingAssist extends Module {

    private final SliderSetting delay;

    private boolean wasSneaking = false;
    private boolean edgeDetected = false;
    private long edgeDetectedTime = 0;

    /** Threshold (in blocks from block center) to consider "near edge". */
    private static final double EDGE_THRESHOLD = 0.3;

    public BridgingAssist() {
        super("BridgingAssist", "Auto-sneaks at block edges while bridging", Category.PLAYER);
        delay = new SliderSetting("Delay", 50, 0, 500, 0);
        addSetting(delay);
    }

    @Override
    public void onTick() {
        // Don't interfere while in GUI
        if (mc.currentScreen != null) {
            releaseSneak();
            return;
        }

        if (mc.thePlayer == null || mc.theWorld == null) return;

        // Don't interfere while flying, in creative flight, or off the ground
        if (mc.thePlayer.capabilities.isFlying) {
            releaseSneak();
            return;
        }
        if (mc.thePlayer.capabilities.isCreativeMode) {
            releaseSneak();
            return;
        }
        if (!mc.thePlayer.onGround) {
            releaseSneak();
            return;
        }

        boolean nearEdge = isNearEdge();

        if (nearEdge) {
            if (!edgeDetected) {
                edgeDetected = true;
                edgeDetectedTime = System.currentTimeMillis();
            }

            long elapsed = System.currentTimeMillis() - edgeDetectedTime;
            if (elapsed >= delay.getValue()) {
                holdSneak();
            }
        } else {
            edgeDetected = false;
            releaseSneak();
        }
    }

    @Override
    public void onDisable() {
        releaseSneak();
        edgeDetected = false;
    }

    private void holdSneak() {
        if (!wasSneaking) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
            wasSneaking = true;
        }
    }

    private void releaseSneak() {
        if (wasSneaking) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            wasSneaking = false;
        }
    }

    /**
     * Checks all four cardinal directions for an edge.
     * An edge exists when the adjacent block (at foot level - 1) is air
     * and the player's position within their current block is near that boundary.
     */
    private boolean isNearEdge() {
        double posX = mc.thePlayer.posX;
        double posY = mc.thePlayer.posY;
        double posZ = mc.thePlayer.posZ;

        int blockX = (int) Math.floor(posX);
        int blockY = (int) Math.floor(posY);
        int blockZ = (int) Math.floor(posZ);

        // Relative position within the current block (0.0 – 1.0)
        double relX = posX - blockX;
        double relZ = posZ - blockZ;

        // Check four adjacent blocks at foot-1 level
        // East (+X), West (-X), South (+Z), North (-Z)
        BlockPos[] neighbors = {
            new BlockPos(blockX + 1, blockY - 1, blockZ),
            new BlockPos(blockX - 1, blockY - 1, blockZ),
            new BlockPos(blockX,     blockY - 1, blockZ + 1),
            new BlockPos(blockX,     blockY - 1, blockZ - 1)
        };

        for (int i = 0; i < 4; i++) {
            if (mc.theWorld.isAirBlock(neighbors[i])) {
                switch (i) {
                    case 0: if (relX > 1.0 - EDGE_THRESHOLD) return true; break; // East
                    case 1: if (relX < EDGE_THRESHOLD) return true; break;       // West
                    case 2: if (relZ > 1.0 - EDGE_THRESHOLD) return true; break; // South
                    case 3: if (relZ < EDGE_THRESHOLD) return true; break;       // North
                }
            }
        }

        return false;
    }
}