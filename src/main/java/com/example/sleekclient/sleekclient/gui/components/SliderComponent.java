package com.example.sleekclient.gui.components;

import com.example.sleekclient.setting.SliderSetting;
import com.example.sleekclient.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

/**
 * Slider component for adjusting numeric SliderSetting values.
 * Supports click-and-drag interaction with a visual track, fill, and knob.
 */
public class SliderComponent extends Component {

    private static final int TRACK_HEIGHT = 2;
    private static final int KNOB_RADIUS  = 4;
    private static final int VALUE_RIGHT_PADDING = 4;
    private static final int NAME_LEFT_PADDING   = 14;

    private final SliderSetting setting;
    private boolean dragging = false;

    public SliderComponent(SliderSetting setting, int x, int y, int width) {
        this.setting = setting;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = 24;
    }

    @Override
    public void draw(int mouseX, int mouseY, float alpha) {
        updateHover(mouseX, mouseY);

        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;

        // Name (left)
        int nameColor = RenderUtil.applyAlpha(0xFFAAAAAA, alpha);
        font.drawString(setting.getName(), x + NAME_LEFT_PADDING, y + 2, nameColor, false);

        // Value (right)
        String valStr = String.format("%." + setting.getDecimals() + "f", setting.getValue());
        int valColor  = RenderUtil.applyAlpha(0xFFFFFFFF, alpha);
        font.drawString(valStr, x + width - font.getStringWidth(valStr) - VALUE_RIGHT_PADDING, y + 2, valColor, false);

        // Track
        int trackY = y + 18;
        int trackColor = RenderUtil.applyAlpha(0xFF2A2A2A, alpha);
        RenderUtil.drawRect(x + NAME_LEFT_PADDING, trackY, x + width - VALUE_RIGHT_PADDING, trackY + TRACK_HEIGHT, trackColor);

        // Fill
        int trackStart = x + NAME_LEFT_PADDING;
        int trackEnd   = x + width - VALUE_RIGHT_PADDING;
        int trackWidth = trackEnd - trackStart;
        int fillEnd    = trackStart + (int) (setting.getProgress() * trackWidth);
        int fillColor  = RenderUtil.applyAlpha(0xFFFFFFFF, alpha);
        RenderUtil.drawRect(trackStart, trackY, fillEnd, trackY + TRACK_HEIGHT, fillColor);

        // Knob
        float knobX = trackStart + setting.getProgress() * trackWidth;
        float knobY = trackY + TRACK_HEIGHT / 2f;
        int knobColor = RenderUtil.applyAlpha(0xFFFFFFFF, alpha);
        RenderUtil.drawCircle(knobX, knobY, KNOB_RADIUS, knobColor);

        // Hover glow on knob
        if (hoverProgress > 0.01f || dragging) {
            float glowAlpha = 0.15f * alpha * (dragging ? 1f : hoverProgress);
            int glowColor = RenderUtil.applyAlpha(0xFFFFFFFF, glowAlpha);
            RenderUtil.drawCircle(knobX, knobY, KNOB_RADIUS + 3, glowColor);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isHovered(mouseX, mouseY)) {
            dragging = true;
            updateValueFromMouse(mouseX);
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int mouseButton) {
        if (dragging) {
            updateValueFromMouse(mouseX);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            dragging = false;
        }
    }

    private void updateValueFromMouse(int mouseX) {
        int trackStart = x + NAME_LEFT_PADDING;
        int trackEnd   = x + width - VALUE_RIGHT_PADDING;
        float progress = (float) (mouseX - trackStart) / (trackEnd - trackStart);
        progress = Math.max(0f, Math.min(1f, progress));
        double value = setting.getMinimum() + progress * (setting.getMaximum() - setting.getMinimum());
        setting.setValue(value);
    }

    public boolean isDragging() {
        return dragging;
    }
}