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
 * LeftClicker — auto-clicks the left mouse button at a configurable CPS
 * with randomized timing for natural feel. Only activates while the
 * physical left button is held. Optional block-break check prevents
 * clicking while the crosshair is on a block.
 */
public class LeftClicker extends Module {

    private final SliderSetting cps;
    private final BooleanSetting blockBreakCheck;

    private Robot robot;
    private long lastClickTime;
    private long nextClickDelay;

    public LeftClicker() {
        super("LeftClicker", "Auto-clicks left mouse at adjustable CPS", Category.COMBAT);
        cps = new SliderSetting("CPS", 12, 1, 20, 0);
        blockBreakCheck = new BooleanSetting("Block Break Check", true);
        addSetting(cps);
        addSetting(blockBreakCheck);

        try {
            robot = new Robot();
        } catch (AWTException e) {
            System.err.println("[SleekClient] Failed to initialize Robot for LeftClicker");
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
        // Safety: don't run in GUI screens or null world
        if (mc.currentScreen != null) return;
        if (mc.thePlayer == null || mc.theWorld == null) return;

        // Only activate while physical left mouse is held
        if (!Mouse.isButtonDown(0)) return;

        // Block break check: skip if looking at a block
        if (blockBreakCheck.getValue()) {
            if (mc.objectMouseOver != null
                && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                return;
            }
        }

        long now = System.currentTimeMillis();
        if (now - lastClickTime >= nextClickDelay) {
            // Reset Minecraft's click cooldown so the simulated click registers
            mc.leftClickCounter = 0;

            // Send OS-level mouse click via Robot
            if (robot != null) {
                robot.mousePress(InputEvent.BUTTON1_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
            }

            lastClickTime = now;

            // Randomize next delay (±20% of base interval)
            double baseDelay = 1000.0 / cps.getValue();
            nextClickDelay = (long) (baseDelay * (0.8 + Math.random() * 0.4));
        }
    }
}