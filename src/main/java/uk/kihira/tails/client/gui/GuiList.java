package uk.kihira.tails.client.gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.kihira.tails.client.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.Tessellator;

import java.util.List;

// todo GuiListExtended is a lot better now, need to refactor this
@OnlyIn(Dist.CLIENT)
public class GuiList<T extends GuiListExtended.IGuiListEntry> extends GuiListExtended {
    private final IListCallback<T> parent;
    private final List<T> entries;
    private int currentIndex;

    public GuiList(IListCallback<T> parent, int width, int height, int top, int bottom, int slotHeight, List<T> entries) {
        super(Minecraft.getInstance(), width, height, top, bottom, slotHeight);
        this.parent = parent;
        this.entries = entries;
        left = -3;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        RenderHelper.startGlScissor(this.left, this.top, this.width + 3, this.height);
        super.drawScreen(mouseX, mouseY, partialTicks);
        RenderHelper.endGlScissor();
    }

    @Override
    protected void drawContainerBackground(Tessellator tessellator) {}

    @Override
    protected void elementClicked(int index, boolean doubleClick, int mouseX, int mouseY) {
        this.currentIndex = index;
        if (this.parent != null) this.parent.onEntrySelected(this, index, this.getListEntry(index));
    }

    @Override
    protected int getScrollBarX() {
        return this.right - 6;
    }

    @Override
    protected boolean isSelected(int index) {
        return this.currentIndex == index;
    }

    @Override
    public T getListEntry(int index) {
        return this.entries.get(index);
    }

    @Override
    public int getListWidth() {
        return width - 8;
    }

    public int getCurrentIndex() {
        return this.currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    public List<T> getEntries() {
        return this.entries;
    }
}
