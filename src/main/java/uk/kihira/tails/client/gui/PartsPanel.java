package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import uk.kihira.tails.client.*;
import uk.kihira.tails.common.PartRegistry;

import java.io.IOException;
import java.util.stream.Collectors;

public class PartsPanel extends Panel<GuiEditor> implements IListCallback<PartsPanel.PartEntry> {

    private GuiList<PartEntry> partList;
    private GuiButton mountPointButton;
    private MountPoint mountPoint; // todo temporary until UI rework. Tabs with search?

    private final FakeEntity fakeEntity;
    private final int listTop = 35;

    PartsPanel(GuiEditor parent, int left, int top, int right, int bottom) {
        super(parent, left, top, right, bottom);
        alwaysReceiveMouse = true;
        mountPoint = MountPoint.CHEST;

        fakeEntity = new FakeEntity(Minecraft.getMinecraft().world);
    }

    @Override
    public void initGui() {
        initPartList();

        buttonList.add(mountPointButton = new GuiButtonExt(0, width/2 - 25, 16, 50, 16, mountPoint.name()));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
        zLevel = -100;
        drawGradientRect(0, 0, width, listTop, 0xEA000000, 0xEA000000);
        drawGradientRect(0, listTop, width, height, 0xCC000000, 0xCC000000);
        zLevel = 0;
        GlStateManager.color(1, 1, 1, 1);
        drawCenteredString(fontRenderer, I18n.format("gui.partselect"), width/2, 5, 0xFFFFFF);
        //Tails list
        partList.drawScreen(mouseX, mouseY, p_73863_3_);

        super.drawScreen(mouseX, mouseY, p_73863_3_);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == mountPointButton.id) {
            if (mountPoint.ordinal() + 1 >= MountPoint.values().length) {
                mountPoint = MountPoint.values()[0];
            }
            else {
                mountPoint = MountPoint.values()[mountPoint.ordinal() + 1];
            }

            initPartList();

            mountPointButton.displayString = mountPoint.name();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        partList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        partList.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void handleMouseInput() throws IOException {
        partList.handleMouseInput();
    }

    @Override
    public void onGuiClosed() {
        dispose();
    }

    @Override
    public boolean onEntrySelected(GuiList guiList, int index, PartEntry entry) {
        return true;
    }

    private void initPartList() {
        dispose();

        this.partList = new GuiList<>(this, width, height - listTop, listTop, height, 55,
                PartRegistry.getPartsByMountPoint(mountPoint).stream().map(PartEntry::new).collect(Collectors.toList()));
        this.partList.width = width;
        selectDefaultListEntry();
    }

    void selectDefaultListEntry() {
        partList.setCurrrentIndex(0);
    }

    private void dispose() {
        if (partList == null) return;
        for (PartEntry entry : partList.getEntries()) {
            entry.outfitPart.dispose();
        }
    }

    private void renderPart(int x, int y, int z, int scale, OutfitPart part) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.scale(-scale, scale, 1F);

        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().getRenderManager().playerViewY = 180.0F;
        PartRegistry.getRenderer(part.basePart).render(fakeEntity, part, 0);
        RenderHelper.disableStandardItemLighting();
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

        GlStateManager.popMatrix();
    }

    class PartEntry implements GuiListExtended.IGuiListEntry {

        final OutfitPart outfitPart;
        final Part part;

        PartEntry(Part part) {
            this.part = part;
            this.outfitPart = new OutfitPart(part);
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
            GlStateManager.color(1, 1, 1, 1);
            zLevel = 0;

            boolean currentPart = partList.getCurrrentIndex() == slotIndex;
            renderPart(right - 25, y - 25, currentPart ? 10 : 1, 50, outfitPart);
            ClientUtils.drawStringMultiLine(fontRenderer, part.name, 5, y + 17, 0xFFFFFF);

            if (currentPart) {
                if (part.author != null) {
                    //Yeah its not nice but eh, works
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(5, y + 27, 0);
                    GlStateManager.scale(0.6F, 0.6F, 1F);
                    zLevel = 100;
                    fontRenderer.drawString(I18n.format("gui.author") + ":", 0, 0, 0xFFFFFF);
                    GlStateManager.translate(0, 10, 0);
                    fontRenderer.drawString(TextFormatting.AQUA + part.author, 0, 0, 0xFFFFFF);
                    GlStateManager.popMatrix();
                    zLevel = 0;
                }
            }
        }

        @Override
        public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {}

        @Override
        public boolean mousePressed(int index, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            // todo add button click
            if (relativeX > 40 && relativeX < 60 && relativeY > 10 && relativeY < 20) {
                parent.addOutfitPart(new OutfitPart(part));
                return true;
            }
            return false;
        }

        @Override
        public void mouseReleased(int index, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {}
    }
}
