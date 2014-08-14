package kihira.tails.client.gui;

import kihira.foxlib.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.Tessellator;

import java.util.List;

public class GuiList extends GuiListExtended {

    private final List<? extends IGuiListEntry> entries;
    private int currrentIndex;

    public GuiList(Minecraft minecraft, int width, int height, int top, int bottom, int slotHeight, List<? extends IGuiListEntry> entries) {
        super(minecraft, width, height, top, bottom, slotHeight);
        this.entries = entries;
    }

    @Override
    public void drawScreen(int p_148128_1_, int p_148128_2_, float p_148128_3_) {
        RenderHelper.startGlScissor(this.left, this.top, this.width, this.height);
        super.drawScreen(p_148128_1_, p_148128_2_, p_148128_3_);
        RenderHelper.endGlScissor();
    }

    @Override
    protected void drawContainerBackground(Tessellator tessellator) {}

    @Override
    protected void elementClicked(int index, boolean doubleClick, int mouseX, int mouseY) {
        this.currrentIndex = index;
    }

    @Override
    protected boolean isSelected(int index) {
        return this.currrentIndex == index;
    }

    @Override
    public IGuiListEntry getListEntry(int index) {
        return this.entries.get(index);
    }

    @Override
    protected int getSize() {
        return this.entries.size();
    }

    public int getCurrrentIndex() {
        return this.currrentIndex;
    }

    public List<? extends IGuiListEntry> getEntries() {
        return this.entries;
    }
}
