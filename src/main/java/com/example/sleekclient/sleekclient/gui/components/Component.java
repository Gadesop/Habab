package com.example.sleekclient.gui.components;

/**
 * Abstract base for all interactive GUI components.
 * Provides position, hover animation state, and input method signatures.
 */
public abstract class Component {

    protected int x, y;
    protected int width, height;
    protected float hoverProgress; // 0 = not hovered, 1 = fully hovered

    public abstract void draw(int mouseX, int mouseY, float alpha);

    public abstract void mouseClicked(int mouseX, int mouseY, int mouseButton);

    public void mouseClickMove(int mouseX, int mouseY, int mouseButton)  { /* override */ }
    public void mouseReleased(int mouseX, int mouseY, int mouseButton)   { /* override */ }

    /** Called every frame to update hover interpolation. */
    public void updateHover(int mouseX, int mouseY) {
        float target = isHovered(mouseX, mouseY) ? 1.0f : 0.0f;
        hoverProgress += (target - hoverProgress) * 0.2f;
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width
            && mouseY >= y && mouseY <= y + height;
    }

    // --- Getters / Setters ---

    public int getX()        { return x; }
    public void setX(int x)  { this.x = x; }
    public int getY()        { return y; }
    public void setY(int y)  { this.y = y; }
    public int getWidth()    { return width; }
    public int getHeight()   { return height; }
}