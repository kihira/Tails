package uk.kihira.tails.client.gui;

import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.Validate;

import java.io.IOException;

public abstract class Panel<T extends GuiBase> extends GuiBaseScreen
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
        super(StringTextComponent.EMPTY);
        Validate.isInstanceOf(GuiBase.class, parent);

        this.parent = parent;
        this.left = x;
        this.top = y;
        this.right = x + width;
        this.bottom = y + height;
    }

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
