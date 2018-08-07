package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import uk.kihira.gltf.Model;
import uk.kihira.tails.client.*;

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
                PartRegistry.getPartsByMountPoint(mountPoint).map(PartEntry::new).collect(Collectors.toList()));
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
        Part basePart = part.getPart();
        if (basePart == null) return; // todo spinny circle
        Model model = basePart.getModel();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.scale(-scale, scale, 1F);

        RenderHelper.enableStandardItemLighting();
        Minecraft.getMinecraft().getRenderManager().playerViewY = 180.0F;
        if (model != null) {
            model.render();
        } else {
            // todo render loading circle
        }
        RenderHelper.disableStandardItemLighting();
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);

        GlStateManager.popMatrix();
    }

    class PartEntry implements GuiListExtended.IGuiListEntry {
        private static final int addX = 1;
        private static final int addY = 40;
        private static final int addWidth = 10;
        private static final int addHeight = 10;
        private static final int addColour = 0xFF666666;

        final OutfitPart outfitPart;
        final Part part;

        PartEntry(Part part) {
            this.part = part;
            this.outfitPart = new OutfitPart(part);
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
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
                    //zLevel = 100;
                    fontRenderer.drawString(I18n.format("gui.author") + ":", 0, 0, 0xFFFFFF);
                    GlStateManager.translate(0, 10, 0);
                    fontRenderer.drawString(TextFormatting.AQUA + part.author, 0, 0, 0xFFFFFF);
                    GlStateManager.popMatrix();
                    //zLevel = 0;
                }
                // Draw "add" button
                GuiUtils.drawGradientRect(0, x+addX, y+addY, x+addX+addWidth, y+addY+addHeight, addColour, addColour);
                fontRenderer.drawString("+", x+addX+(addWidth/4), y+addY+(addHeight/4), 0xFFFFFF);
                //GuiUtils.drawContinuousTexturedBox(new ResourceLocation("textures/gui/widgets.png"), x+addX, y+addY, 0, 66, addWidth, addHeight, 200, 20, 2, zLevel);
            }
        }

        @Override
        public void updatePosition(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_) {}

        @Override
        public boolean mousePressed(int index, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            if (relativeX > addX && relativeX < addX+addWidth && relativeY > addY && relativeY < addY+addHeight) {
                parent.addOutfitPart(new OutfitPart(part));
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
            return false;
        }

        @Override
        public void mouseReleased(int index, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {}
    }
}
