package com.example.sleekclient.module.visuals;

import com.example.sleekclient.module.Category;
import com.example.sleekclient.module.Module;
import com.example.sleekclient.setting.BooleanSetting;
import com.example.sleekclient.setting.SliderSetting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

/**
 * HitboxESP — draws clean wireframe boxes around other players.
 * Supports invisible player detection, adjustable box opacity/width,
 * and optional through-walls rendering.
 */
public class HitboxESP extends Module {

    private final BooleanSetting showInvisible;
    private final SliderSetting  boxOpacity;
    private final SliderSetting  boxWidth;
    private final BooleanSetting renderThroughWalls;

    public HitboxESP() {
        super("HitboxESP", "Draws ESP boxes around players", Category.VISUALS);
        showInvisible      = new BooleanSetting("Show Invisible", true);
        boxOpacity         = new SliderSetting("Box Opacity", 0.5, 0.1, 1.0, 2);
        boxWidth           = new SliderSetting("Box Width", 1.5, 0.5, 5.0, 1);
        renderThroughWalls = new BooleanSetting("Through Walls", false);
        addSetting(showInvisible);
        addSetting(boxOpacity);
        addSetting(boxWidth);
        addSetting(renderThroughWalls);
    }

    @Override
    public void onRender3D(float partialTicks) {
        if (mc.theWorld == null || mc.thePlayer == null) return;

        double renderX = mc.getRenderManager().renderPosX;
        double renderY = mc.getRenderManager().renderPosY;
        double renderZ = mc.getRenderManager().renderPosZ;

        // Pre-render state
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth((float) boxWidth.getValue());

        if (renderThroughWalls.getValue()) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }

        float alpha = (float) boxOpacity.getValue();

        for (EntityPlayer player : mc.theWorld.playerEntities) {
            if (player == mc.thePlayer) continue;
            if (player.isDead) continue;
            if (player.isInvisible() && !showInvisible.getValue()) continue;

            drawPlayerBox(player, partialTicks, renderX, renderY, renderZ, alpha);
        }

        // Restore state
        if (renderThroughWalls.getValue()) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }
        GL11.glLineWidth(1.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    /**
     * Draws a wireframe box around a single player using GL_LINES.
     */
    private void drawPlayerBox(EntityPlayer player, float partialTicks,
                               double renderX, double renderY, double renderZ,
                               float alpha) {

        // Interpolated position
        double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks - renderX;
        double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks - renderY;
        double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks - renderZ;

        float hw = player.width / 2.0f;
        float h  = player.height;

        GL11.glBegin(GL11.GL_LINES);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);

        // Bottom rectangle
        GL11.glVertex3d(x - hw, y,     z - hw);  GL11.glVertex3d(x + hw, y,     z - hw);
        GL11.glVertex3d(x + hw, y,     z - hw);  GL11.glVertex3d(x + hw, y,     z + hw);
        GL11.glVertex3d(x + hw, y,     z + hw);  GL11.glVertex3d(x - hw, y,     z + hw);
        GL11.glVertex3d(x - hw, y,     z + hw);  GL11.glVertex3d(x - hw, y,     z - hw);

        // Top rectangle
        GL11.glVertex3d(x - hw, y + h, z - hw);  GL11.glVertex3d(x + hw, y + h, z - hw);
        GL11.glVertex3d(x + hw, y + h, z - hw);  GL11.glVertex3d(x + hw, y + h, z + hw);
        GL11.glVertex3d(x + hw, y + h, z + hw);  GL11.glVertex3d(x - hw, y + h, z + hw);
        GL11.glVertex3d(x - hw, y + h, z + hw);  GL11.glVertex3d(x - hw, y + h, z - hw);

        // Vertical edges
        GL11.glVertex3d(x - hw, y,     z - hw);  GL11.glVertex3d(x - hw, y + h, z - hw);
        GL11.glVertex3d(x + hw, y,     z - hw);  GL11.glVertex3d(x + hw, y + h, z - hw);
        GL11.glVertex3d(x + hw, y,     z + hw);  GL11.glVertex3d(x + hw, y + h, z + hw);
        GL11.glVertex3d(x - hw, y,     z + hw);  GL11.glVertex3d(x - hw, y + h, z + hw);

        GL11.glEnd();
        GL11.glColor4f(1f, 1f, 1f, 1f);
    }
}