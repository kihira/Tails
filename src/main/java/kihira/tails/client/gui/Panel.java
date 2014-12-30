package kihira.tails.client.gui;

import kihira.foxlib.client.gui.GuiBaseScreen;

public abstract class Panel<T extends GuiBase> extends GuiBaseScreen {

    protected final T parent;
    public int left;
    public int top;
    public int right;
    public int bottom;
    public boolean alwaysReceiveMouse = false;
    public boolean enabled = true;

    public Panel(T parent, int left, int top, int width, int height) {
        this.parent = parent;
        this.left = left;
        this.top = top;
        this.right = left + width;
        this.bottom = top + height;
    }

    public void resize(int x, int y, int newWidth, int newHeight) {
        left = x;
        top = y;
        right = x + newWidth;
        bottom = y + newHeight;
    }

    //All this stuff is to make them public
    public void keyTyped(char key, int keycode) {}

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public void mouseMovedOrUp(int mouseX, int mouseY, int mouseButton) {
        super.mouseMovedOrUp(mouseX, mouseY, mouseButton);
    }

    public void mouseClickMove(int mouseX, int mouseY, int mouseButton, long pressTime) {
        super.mouseClickMove(mouseX, mouseY, mouseButton, pressTime);
    }
}
