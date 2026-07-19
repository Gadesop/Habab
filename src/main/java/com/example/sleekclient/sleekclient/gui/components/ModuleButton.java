package com.example.sleekclient.gui.components;

import com.example.sleekclient.module.Module;
import com.example.sleekclient.setting.BooleanSetting;
import com.example.sleekclient.setting.Setting;
import com.example.sleekclient.setting.SliderSetting;
import com.example.sleekclient.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * Component representing a module entry in the GUI.
 * Contains a clickable header (toggles the module) and
 * child setting components rendered below.
 */
public class ModuleButton extends Component {

    private static final int HEADER_HEIGHT = 22;
    private static final int SETTING_INDENT = 12;
    private static final int SETTING_GAP = 2;
    private static final int TOGGLE_W = 30;
    private static final int TOGGLE_H = 12;
    private static final int KNOB_R = 4;

    private final Module module;
    private final List<Component> settingComponents = new ArrayList<>();

    public ModuleButton(Module module, int x, int y, int width) {
        this.module = module;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = HEADER_HEIGHT;

        // Build child setting components
        int sy = y + HEADER_HEIGHT + SETTING_GAP;
        for (Setting<?> setting : module.getSettings()) {
            Component comp;
            if (setting instanceof SliderSetting) {
                comp = new SliderComponent((SliderSetting) setting, x + SETTING_INDENT, sy, width - SETTING_INDENT * 2);
            } else if (setting instanceof BooleanSetting) {
                comp = new ToggleComponent((BooleanSetting) setting, x + SETTING_INDENT, sy, width - SETTING_INDENT * 2);
            } else {
                continue;
            }
            settingComponents.add(comp);
            sy += comp.getHeight() + SETTING_GAP;
        }
    }

    /** Total height including header + all setting rows. */
    public int getTotalHeight() {
        int total = HEADER_HEIGHT;
        for (Component c : settingComponents) {
            total += c.getHeight() + SETTING_GAP;
        }
        return total;
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        // Reposition children
        int sy = y + HEADER_HEIGHT + SETTING_GAP;
        for (Component c : settingComponents) {
            c.setY(sy);
            sy += c.getHeight() + SETTING_GAP;
        }
    }

    @Override
    public void draw(int mouseX, int mouseY, float alpha) {
        updateHover(mouseX, mouseY);

        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
        boolean on = module.isEnabled();

        // Header background (subtle highlight if enabled or hovered)
        int bgAlpha = 0xFF;
        if (on) {
            int bg = RenderUtil.applyAlpha(0xFF141414, alpha);
            RenderUtil.drawRect(x, y, x + width, y + HEADER_HEIGHT, bg);
        }
        if (hoverProgress > 0.01f) {
            int hover = RenderUtil.applyAlpha(0x18FFFFFF, alpha * hoverProgress);
            RenderUtil.drawRect(x, y, x + width, y + HEADER_HEIGHT, hover);
        }

        // Module name
        int nameColor = on
            ? RenderUtil.applyAlpha(0xFFFFFFFF, alpha)
            : RenderUtil.applyAlpha(0xFF888888, alpha);
        font.drawString(module.getName(), x + 8, y + 7, nameColor, false);

        // Toggle pill (right side of header)
        int toggleX = x + width - TOGGLE_W - 8;
        int toggleY = y + (HEADER_HEIGHT - TOGGLE_H) / 2;
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

        // Draw setting components
        for (Component c : settingComponents) {
            c.draw(mouseX, mouseY, on ? alpha : alpha * 0.5f);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton != 0) return;

        // Check setting components first (they take priority)
        for (Component c : settingComponents) {
            if (c.isHovered(mouseX, mouseY)) {
                c.mouseClicked(mouseX, mouseY, mouseButton);
                return;
            }
        }

        // Check header area
        if (mouseX >= x && mouseX <= x + width
            && mouseY >= y && mouseY <= y + HEADER_HEIGHT) {
            module.toggle();
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int mouseButton) {
        for (Component c : settingComponents) {
            c.mouseClickMove(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (Component c : settingComponents) {
            c.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }
}