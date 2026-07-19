package com.example.sleekclient.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

/**
 * Utility class for GUI and world rendering helpers.
 * All methods are static and use Minecraft's native rendering system.
 */
public class RenderUtil {

    /**
     * Draws a rounded rectangle using a GL triangle fan.
     *
     * @param x      top-left X
     * @param y      top-left Y
     * @param width  total width
     * @param height total height
     * @param radius corner radius
     * @param color  ARGB color int
     */
    public static void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
        float alpha = (color >> 24 & 255) / 255.0f;
        float r     = (color >> 16 & 255) / 255.0f;
        float g     = (color >> 8 & 255) / 255.0f;
        float b     = (color & 255) / 255.0f;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(r, g, b, alpha);

        GL11.glBegin(GL11.GL_TRIANGLE_FAN);

        // Center vertex
        GL11.glVertex2f(x + width / 2f, y + height / 2f);

        // Top-left arc (180° → 270°)
        for (int i = 180; i <= 270; i += 5) {
            double angle = Math.toRadians(i);
            GL11.glVertex2f(
                x + radius + (float) (Math.cos(angle) * radius),
                y + radius + (float) (Math.sin(angle) * radius)
            );
        }
        // Top-right arc (270° → 360°)
        for (int i = 270; i <= 360; i += 5) {
            double angle = Math.toRadians(i);
            GL11.glVertex2f(
                x + width - radius + (float) (Math.cos(angle) * radius),
                y + radius + (float) (Math.sin(angle) * radius)
            );
        }
        // Bottom-right arc (0° → 90°)
        for (int i = 0; i <= 90; i += 5) {
            double angle = Math.toRadians(i);
            GL11.glVertex2f(
                x + width - radius + (float) (Math.cos(angle) * radius),
                y + height - radius + (float) (Math.sin(angle) * radius)
            );
        }
        // Bottom-left arc (90° → 180°)
        for (int i = 90; i <= 180; i += 5) {
            double angle = Math.toRadians(i);
            GL11.glVertex2f(
                x + radius + (float) (Math.cos(angle) * radius),
                y + height - radius + (float) (Math.sin(angle) * radius)
            );
        }

        GL11.glEnd();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    /**
     * Draws a filled circle using a GL triangle fan.
     *
     * @param cx     center X
     * @param cy     center Y
     * @param radius circle radius
     * @param color  ARGB color int
     */
    public static void drawCircle(float cx, float cy, float radius, int color) {
        float alpha = (color >> 24 & 255) / 255.0f;
        float r     = (color >> 16 & 255) / 255.0f;
        float g     = (color >> 8 & 255) / 255.0f;
        float b     = (color & 255) / 255.0f;

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(r, g, b, alpha);

        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2f(cx, cy);
        for (int i = 0; i <= 360; i += 5) {
            double angle = Math.toRadians(i);
            GL11.glVertex2f(
                cx + (float) (Math.cos(angle) * radius),
                cy + (float) (Math.sin(angle) * radius)
            );
        }
        GL11.glEnd();

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    /**
     * Multiplies the alpha channel of an ARGB color by a factor (0–1).
     */
    public static int applyAlpha(int color, float alpha) {
        int a = (color >> 24) & 0xFF;
        a = Math.max(0, Math.min(255, (int) (a * alpha)));
        return (a << 24) | (color & 0x00FFFFFF);
    }

    /**
     * Convenience wrapper around Gui.drawRect with alpha adjustment.
     */
    public static void drawRect(int left, int top, int right, int bottom, int color) {
        Gui.drawRect(left, top, right, bottom, color);
    }
}