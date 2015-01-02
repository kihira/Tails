package kihira.tails.client.gui;

import kihira.foxlib.client.gui.GuiBaseScreen;
import org.apache.commons.lang3.Validate;

public abstract class Panel<T extends GuiBase> extends GuiBaseScreen {

    protected final T parent;
    public int left;
    public int top;
    public int right;
    public int bottom;
    public boolean alwaysReceiveMouse = false;
    public boolean enabled = true;

    public Panel(T parent, int x, int y, int width, int height) {
        Validate.isInstanceOf(GuiBase.class, parent);

        this.parent = parent;
        this.left = x;
        this.top = y;
        this.right = x + width;
        this.bottom = y + height;
    }

    public void resize(int x, int y, int newWidth, int newHeight) {
        left = x;
        top = y;
        right = x + newWidth;
        bottom = y + newHeight;
    }

    public void setHeight(int height) {
        this.height = height;
        bottom = top + height;
    }

    public void setWidth(int width) {
        this.width = width;
        right = left + width;
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
