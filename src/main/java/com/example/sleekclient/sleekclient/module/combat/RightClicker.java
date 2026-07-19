package com.example.sleekclient.module.combat;

import com.example.sleekclient.module.Category;
import com.example.sleekclient.module.Module;
import com.example.sleekclient.setting.BooleanSetting;
import com.example.sleekclient.setting.SliderSetting;
import net.minecraft.util.MovingObjectPosition;
import org.lwjgl.input.Mouse;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

/**
 * RightClicker — auto-clicks the right mouse button at a configurable CPS
 * with randomized timing. Only activates while the physical right button
 * is held. Block-interaction check prevents interfering with block placement.
 */
public class RightClicker extends Module {

    private final SliderSetting cps;
    private final BooleanSetting blockInteractionCheck;

    private Robot robot;
    private long lastClickTime;
    private long nextClickDelay;

    public RightClicker() {
        super("RightClicker", "Auto-clicks right mouse at adjustable CPS", Category.COMBAT);
        cps = new SliderSetting("CPS", 10, 1, 20, 0);
        blockInteractionCheck = new BooleanSetting("Block Interaction Check", true);
        addSetting(cps);
        addSetting(blockInteractionCheck);

        try {
            robot = new Robot();
        } catch (AWTException e) {
            System.err.println("[SleekClient] Failed to initialize Robot for RightClicker");
        }
    }

    @Override
    public void onEnable() {
        lastClickTime = 0;
        nextClickDelay = 0;
    }

    @Override
    public void onDisable() {
        lastClickTime = 0;
        nextClickDelay = 0;
    }

    @Override
    public void onTick() {
        if (mc.currentScreen != null) return;
        if (mc.thePlayer == null || mc.theWorld == null) return;

        // Only activate while physical right mouse is held
        if (!Mouse.isButtonDown(1)) return;

        // Block interaction check: skip if looking at a block
        if (blockInteractionCheck.getValue()) {
            if (mc.objectMouseOver != null
                && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                return;
            }
        }

        long now = System.currentTimeMillis();
        if (now - lastClickTime >= nextClickDelay) {
            // Reset Minecraft's right-click delay so the simulated click registers
            mc.rightClickDelayTimer = 0;

            // Send OS-level right click via Robot
            if (robot != null) {
                robot.mousePress(InputEvent.BUTTON3_MASK);
                robot.mouseRelease(InputEvent.BUTTON3_MASK);
            }

            lastClickTime = now;

            // Randomize next delay (±20% of base interval)
            double baseDelay = 1000.0 / cps.getValue();
            nextClickDelay = (long) (baseDelay * (0.8 + Math.random() * 0.4));
        }
    }
}