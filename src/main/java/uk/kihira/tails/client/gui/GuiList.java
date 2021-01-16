package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.kihira.tails.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class GuiList<T extends AbstractList.AbstractListEntry<T>> extends AbstractList<T>
{
    private final IListCallback<T> parent;
    private final List<T> entries;

    public GuiList(IListCallback<T> parent, int width, int height, int top, int bottom, int slotHeight, List<T> entries)
    {
        super(Minecraft.getInstance(), width, height, top, bottom, slotHeight);
        this.parent = parent;
        this.entries = entries;
        this.x0 = -3;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        RenderHelper.startGlScissor(this.x0, this.y0, this.width + 3, this.height);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        RenderHelper.endGlScissor();
    }

    @Override
    protected int getScrollbarPosition()
    {
        return this.x1 - 6;
    }

    @Override
    public int getRowWidth()
    {
        return this.width - 8;
    }

    public int getCurrentIndex()
    {
        return this.currentIndex;
    }

    public void setCurrentIndex(int currentIndex)
    {
        this.currentIndex = currentIndex;
    }

    public List<T> getEntries()
    {
        return this.entries;
    }
}
