package com.example.sleekclient.gui;

import com.example.sleekclient.SleekClient;
import com.example.sleekclient.gui.components.Component;
import com.example.sleekclient.gui.components.ModuleButton;
import com.example.sleekclient.module.Category;
import com.example.sleekclient.module.Module;
import com.example.sleekclient.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Main GUI screen. Renders a dark panel with category tabs,
 * module buttons with settings, smooth fade transitions between
 * categories, hover animations, and scroll support.
 */
public class SleekGui extends GuiScreen {

    // --- Layout constants ---
    private static final int WINDOW_WIDTH  = 360;
    private static final int WINDOW_HEIGHT = 260;
    private static final int HEADER_HEIGHT = 36;
    private static final int TAB_WIDTH     = 80;
    private static final int MODULE_SPACING = 8;

    // --- Colors ---
    private static final int COLOR_BG       = 0xF00A0A0A; // 94% opacity black
    private static final int COLOR_BORDER   = 0xFF2A2A2A;
    private static final int COLOR_HEADER   = 0xFF121212;
    private static final int COLOR_TEXT     = 0xFFFFFFFF;
    private static final int COLOR_TEXT_DIM = 0xFF888888;
    private static final int COLOR_TAB_ACTIVE  = 0xFF1A1A1A;
    private static final int COLOR_DIVIDER    = 0xFF1A1A1A;

    // --- State ---
    private int windowX, windowY;
    private Category currentCategory = Category.COMBAT;
    private float switchProgress = 1.0f; // 0 = just switched, 1 = fully visible

    private final List<ModuleButton> components = new ArrayList<>();
    private int scrollOffset = 0;
    private int maxScroll = 0;

    // --- Tab hover tracking ---
    private final float[] tabHoverProgress = new float[Category.values().length];

    @Override
    public void initGui() {
        windowX = (width - WINDOW_WIDTH) / 2;
        windowY = (height - WINDOW_HEIGHT) / 2;
        switchProgress = 1.0f;
        buildComponents();
    }

    /** Rebuild the component list for the current category. */
    private void buildComponents() {
        components.clear();
        int y = windowY + HEADER_HEIGHT + 6 - scrollOffset;
        for (Module module : SleekClient.getInstance().getModuleManager().getModulesByCategory(currentCategory)) {
            ModuleButton btn = new ModuleButton(module, windowX + 10, y, WINDOW_WIDTH - 20);
            components.add(btn);
            y += btn.getTotalHeight() + MODULE_SPACING;
        }
        // Compute max scroll
        int contentHeight = y - (windowY + HEADER_HEIGHT + 6) + scrollOffset;
        int visibleHeight = WINDOW_HEIGHT - HEADER_HEIGHT - 16;
        maxScroll = Math.max(0, contentHeight - visibleHeight);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Dim background
        drawDefaultBackground();

        // Update fade progress
        if (switchProgress < 1.0f) {
            switchProgress += 0.08f;
            if (switchProgress > 1.0f) switchProgress = 1.0f;
        }
        // Smoothstep easing for natural feel
        float eased = switchProgress * switchProgress * (3 - 2 * switchProgress);

        // --- Draw window ---
        drawWindow(eased);

        // --- Draw header (title + tabs) ---
        drawHeader(mouseX, mouseY, eased);

        // --- Draw content area ---
        drawContent(mouseX, mouseY, eased);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawWindow(float alpha) {
        // Outer border
        int borderColor = RenderUtil.applyAlpha(COLOR_BORDER, alpha);
        RenderUtil.drawRect(windowX - 1, windowY - 1,
                windowX + WINDOW_WIDTH + 1, windowY + WINDOW_HEIGHT + 1, borderColor);

        // Background
        int bgColor = RenderUtil.applyAlpha(COLOR_BG, alpha);
        RenderUtil.drawRect(windowX, windowY,
                windowX + WINDOW_WIDTH, windowY + WINDOW_HEIGHT, bgColor);

        // Header background
        int headerColor = RenderUtil.applyAlpha(COLOR_HEADER, alpha);
        RenderUtil.drawRect(windowX, windowY,
                windowX + WINDOW_WIDTH, windowY + HEADER_HEIGHT, headerColor);

        // Divider line under header
        int dividerColor = RenderUtil.applyAlpha(COLOR_DIVIDER, alpha);
        RenderUtil.drawRect(windowX, windowY + HEADER_HEIGHT,
                windowX + WINDOW_WIDTH, windowY + HEADER_HEIGHT + 1, dividerColor);
    }

    private void drawHeader(int mouseX, int mouseY, float alpha) {
        FontRenderer font = Minecraft.getMinecraft().fontRendererObj;

        // Title
        int titleColor = RenderUtil.applyAlpha(COLOR_TEXT, alpha);
        font.drawString("SleekClient", windowX + 12, windowY + 13, titleColor, true);

        // Tabs
        Category[] cats = Category.values();
        int tabStartX = windowX + WINDOW_WIDTH - cats.length * TAB_WIDTH - 10;
        for (int i = 0; i < cats.length; i++) {
            Category cat = cats[i];
            int tabX = tabStartX + i * TAB_WIDTH;
            int tabY = windowY + 8;
            int tabH = HEADER_HEIGHT - 16;

            // Update hover progress
            boolean hovered = mouseX >= tabX && mouseX <= tabX + TAB_WIDTH - 4
                           && mouseY >= tabY && mouseY <= tabY + tabH;
            float target = hovered ? 1.0f : 0.0f;
            tabHoverProgress[i] += (target - tabHoverProgress[i]) * 0.2f;

            boolean active = cat == currentCategory;

            // Tab text
            int tabColor;
            if (active) {
                tabColor = RenderUtil.applyAlpha(COLOR_TEXT, alpha);
            } else {
                tabColor = RenderUtil.applyAlpha(COLOR_TEXT_DIM, alpha);
            }
            String label = cat.getDisplayName();
            int textW = font.getStringWidth(label);
            font.drawString(label, tabX + (TAB_WIDTH - 4 - textW) / 2, tabY + (tabH - font.FONT_HEIGHT) / 2 + 1, tabColor, false);

            // Active underline
            if (active) {
                int underlineColor = RenderUtil.applyAlpha(0xFFFFFFFF, alpha);
                RenderUtil.drawRect(tabX + 8, tabY + tabH + 1, tabX + TAB_WIDTH - 12, tabY + tabH + 2, underlineColor);
            }

            // Hover highlight
            if (tabHoverProgress[i] > 0.01f && !active) {
                int hoverColor = RenderUtil.applyAlpha(0x15FFFFFF, alpha * tabHoverProgress[i]);
                RenderUtil.drawRect(tabX, tabY, tabX + TAB_WIDTH - 4, tabY + tabH, hoverColor);
            }
        }
    }

    private void drawContent(int mouseX, int mouseY, float alpha) {
        // Update component positions based on scroll
        int y = windowY + HEADER_HEIGHT + 6 - scrollOffset;
        for (ModuleButton btn : components) {
            btn.setY(y);
            y += btn.getTotalHeight() + MODULE_SPACING;
        }

        // Scissor test to clip content to the window
        int scissorX = windowX + 1;
        int scissorY = height - (windowY + WINDOW_HEIGHT - 1);
        int scissorW = WINDOW_WIDTH - 2;
        int scissorH = WINDOW_HEIGHT - HEADER_HEIGHT - 2;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorX, scissorY, scissorW, scissorH);

        for (ModuleButton btn : components) {
            btn.draw(mouseX, mouseY, alpha);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        // Scrollbar
        if (maxScroll > 0) {
            int barX = windowX + WINDOW_WIDTH - 4;
            int barY = windowY + HEADER_HEIGHT + 2;
            int barH = WINDOW_HEIGHT - HEADER_HEIGHT - 4;
            float scrollProgress = (float) scrollOffset / maxScroll;
            int knobH = Math.max(20, (int) (barH * ((float) (WINDOW_HEIGHT - HEADER_HEIGHT - 4) / (maxScroll + WINDOW_HEIGHT - HEADER_HEIGHT - 4))));
            int knobY = barY + (int) ((barH - knobH) * scrollProgress);
            int barColor = RenderUtil.applyAlpha(0xFF1A1A1A, alpha);
            int knobColor = RenderUtil.applyAlpha(0xFF3A3A3A, alpha);
            RenderUtil.drawRect(barX, barY, barX + 2, barY + barH, barColor);
            RenderUtil.drawRect(barX, knobY, barX + 2, knobY + knobH, knobColor);
        }
    }

    // --- Input handling ---

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton != 0) return;

        // Check tab clicks
        Category[] cats = Category.values();
        int tabStartX = windowX + WINDOW_WIDTH - cats.length * TAB_WIDTH - 10;
        for (int i = 0; i < cats.length; i++) {
            int tabX = tabStartX + i * TAB_WIDTH;
            int tabY = windowY + 8;
            int tabH = HEADER_HEIGHT - 16;
            if (mouseX >= tabX && mouseX <= tabX + TAB_WIDTH - 4
                && mouseY >= tabY && mouseY <= tabY + tabH) {
                if (cats[i] != currentCategory) {
                    currentCategory = cats[i];
                    switchProgress = 0;
                    scrollOffset = 0;
                    buildComponents();
                }
                return;
            }
        }

        // Check module/setting clicks (within window content area)
        if (mouseX >= windowX && mouseX <= windowX + WINDOW_WIDTH
            && mouseY >= windowY + HEADER_HEIGHT && mouseY <= windowY + WINDOW_HEIGHT) {
            for (ModuleButton btn : components) {
                btn.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long timeSinceLastClick) {
        for (ModuleButton btn : components) {
            btn.mouseClickMove(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int state) {
        if (state >= 0) {
            for (ModuleButton btn : components) {
                btn.mouseReleased(mouseX, mouseY, state);
            }
        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0 && maxScroll > 0) {
            int direction = wheel > 0 ? -1 : 1;
            scrollOffset += direction * 20;
            scrollOffset = Math.max(0, Math.min(maxScroll, scrollOffset));
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_RSHIFT) {
            Minecraft.getMinecraft().displayGuiScreen(null);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        // Save config when GUI closes
        SleekClient.getInstance().getConfigManager().save();
    }
}