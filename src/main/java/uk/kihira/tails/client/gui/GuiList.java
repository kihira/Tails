package uk.kihira.tails.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import uk.kihira.tails.client.RenderHelper;
import net.minecraft.client.Minecraft;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GuiList<E extends ObjectSelectionList.Entry<E>> extends ObjectSelectionList<E>
{
    private final IListCallback<E> parent;

    public GuiList(IListCallback<E> parent, int width, int top, int bottom, int slotHeight, List<E> entries)
    {
        super(Minecraft.getInstance(), width, bottom - top, top, slotHeight);
        this.parent = parent;
        this.setX(-3);

        entries.forEach(this::addEntry);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        RenderHelper.startGlScissor(this.getX(), this.getY(), this.width + 3, this.height);
        super.render(graphics, mouseX, mouseY, partialTicks);
        RenderHelper.endGlScissor();
    }

    @Override
    protected int getScrollbarPosition()
    {
        return this.getRowWidth() - 6;
    }

    @Override
    public int getRowWidth()
    {
        return this.width - 8;
    }

    public void setDefault()
    {
        setSelected(getEntry(0));
    }
}
