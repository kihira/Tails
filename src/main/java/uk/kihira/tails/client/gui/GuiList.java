package uk.kihira.tails.client.gui;

import uk.kihira.tails.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.Tessellator;

import java.util.List;

public class GuiList<T extends GuiListExtended.IGuiListEntry> extends GuiListExtended {

    private final IListCallback<T> parent;
    private final List<T> entries;
    private int currrentIndex;

    public GuiList(IListCallback<T> parent, int width, int height, int top, int bottom, int slotHeight, List<T> entries) {
        super(Minecraft.getMinecraft(), width, height, top, bottom, slotHeight);
        this.parent = parent;
        this.entries = entries;
        left = -3;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float p_148128_3_) {
        RenderHelper.startGlScissor(this.left, this.top, this.width + 3, this.height);
        super.drawScreen(mouseX, mouseY, p_148128_3_);
        RenderHelper.endGlScissor();
    }

    @Override
    protected void drawContainerBackground(Tessellator tessellator) {}

    @Override
    protected void elementClicked(int index, boolean doubleClick, int mouseX, int mouseY) {
        this.currrentIndex = index;
        if (this.parent != null) this.parent.onEntrySelected(this, index, this.getListEntry(index));
    }

    @Override
    protected int getScrollBarX() {
        return this.right - 6;
    }

    @Override
    protected boolean isSelected(int index) {
        return this.currrentIndex == index;
    }

    @Override
    public T getListEntry(int index) {
        return this.entries.get(index);
    }

    @Override
    protected int getSize() {
        return this.entries.size();
    }

    @Override
    public int getListWidth() {
        return width - 8;
    }

    public int getCurrrentIndex() {
        return this.currrentIndex;
    }

    public void setCurrrentIndex(int currrentIndex) {
        this.currrentIndex = currrentIndex;
    }

    public List<T> getEntries() {
        return this.entries;
    }
}
