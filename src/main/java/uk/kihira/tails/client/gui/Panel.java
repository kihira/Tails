package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class Panel<T extends Screen> extends GuiBaseScreen
{
    protected final T parent;
    public int left;
    public int top;
    public int right;
    public int bottom;
    public boolean alwaysReceiveMouse = false;
    public boolean enabled = true;

    public Panel(T parent, int x, int y, int width, int height)
    {
        super(Component.empty());
        //Validate.isInstanceOf(GuiBase.class, parent);

        this.minecraft = Minecraft.getInstance();
        this.font = this.minecraft.font;

        this.parent = parent;
        this.left = x;
        this.top = y;
        this.right = x + width;
        this.bottom = y + height;
    }

    // Override to make it public so we can call it, very much a hack for now
    @Override
    public void init() {}

    public void resize(int x, int y, int newWidth, int newHeight)
    {
        this.left = x;
        this.top = y;
        this.right = x + newWidth;
        this.bottom = y + newHeight;
    }

    public void setHeight(int height)
    {
        this.height = height;
        this.bottom = this.top + height;
    }

    public void setWidth(int width)
    {
        this.width = width;
        this.right = this.left + width;
    }
}
