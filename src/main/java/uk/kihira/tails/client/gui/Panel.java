package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public abstract class Panel<T extends Screen> extends AbstractContainerWidget
{
    protected final T parent;
    public int left;
    public int top;
    public int right;
    public int bottom;
    public boolean alwaysReceiveMouse = false;
    public boolean enabled = true;

    protected List<AbstractWidget> children;

    public Panel(T parent, int x, int y, int width, int height)
    {
        super(x, y, width, height, Component.empty());

        this.parent = parent;
        this.left = x;
        this.top = y;
        this.right = x + width;
        this.bottom = y + height;

        this.children = new ArrayList<>();
    }

    public void addChild(AbstractWidget child)
    {
        this.children.add(child);
    }

    public void resize(int x, int y, int newWidth, int newHeight)
    {
        this.left = x;
        this.top = y;
        this.right = x + newWidth;
        this.bottom = y + newHeight;
    }

    protected Font font()
    {
        return this.parent.getMinecraft().font;
    }

    protected Minecraft minecraft()
    {
        return this.parent.getMinecraft();
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        for (var child : children)
        {
            child.render(graphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY)
    {
        for (var child : children)
        {
            if (child.mouseScrolled(mouseX, mouseY, scrollX, scrollY))
            {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public @NonNull List<? extends GuiEventListener> children()
    {
        return children;
    }

    @Override
    public void setHeight(int height)
    {
        this.height = height;
        this.bottom = this.top + height;
    }

    @Override
    public void setWidth(int width)
    {
        this.width = width;
        this.right = this.left + width;
    }
}
