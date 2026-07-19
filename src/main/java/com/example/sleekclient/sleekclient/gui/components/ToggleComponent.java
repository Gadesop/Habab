package com.example.sleekclient.gui.components;

import com.example.sleekclient.setting.BooleanSetting;
import com.example.sleekclient.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

/**
 * Toggle component for boolean settings.
 * Renders a pill-shaped switch with a sliding knob.
 */
public class ToggleComponent extends Component {

    private static final int TOGGLE_W = 28;
    private static final int TOGGLE_H = 12;
    private static final int KNOB_R   = 4;
    private static final int NAME_LEFT_PADDING = 14;

    private final BooleanSetting setting;

    public ToggleComponent(BooleanSetting setting, int x, int y, int width) {
        this.setting = setting;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = 16;
    }

    @Override
    public void draw(int mouseX, int mouseY, float alpha) {
        updateHover(mouseX, mouseY);

        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;

        // Name (left)
        int nameColor = RenderUtil.applyAlpha(0xFFAAAAAA, alpha);
        font.drawString(setting.getName(), x + NAME_LEFT_PADDING, y + 4, nameColor, false);

        // Toggle pill (right)
        int toggleX = x + width - TOGGLE_W - 4;
        int toggleY = y + 2;

        boolean on = setting.getValue();
        int pillColor = on
            ? RenderUtil.applyAlpha(0xFFFFFFFF, alpha)
            : RenderUtil.applyAlpha(0xFF1A1A1A, alpha);

        RenderUtil.drawRoundedRect(toggleX, toggleY, TOGGLE_W, TOGGLE_H, TOGGLE_H / 2f, pillColor);

        // Knob
        float knobX = on ? toggleX + TOGGLE_W - TOGGLE_H / 2f : toggleX + TOGGLE_H / 2f;
        float knobY = toggleY + TOGGLE_H / 2f;
        int knobColor = on
            ? RenderUtil.applyAlpha(0xFF0A0A0A, alpha)
            : RenderUtil.applyAlpha(0xFF666666, alpha);
        RenderUtil.drawCircle(knobX, knobY, KNOB_R, knobColor);

        // Hover glow
        if (hoverProgress > 0.01f) {
            int glowColor = RenderUtil.applyAlpha(0x15FFFFFF, alpha * hoverProgress);
            RenderUtil.drawRoundedRect(toggleX - 2, toggleY - 2, TOGGLE_W + 4, TOGGLE_H + 4, (TOGGLE_H + 4) / 2f, glowColor);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isHovered(mouseX, mouseY)) {
            setting.setValue(!setting.getValue());
        }
    }
}