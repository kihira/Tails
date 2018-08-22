package uk.kihira.tails.client.gui;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import uk.kihira.gltf.Model;
import uk.kihira.tails.client.*;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.proxy.ClientProxy;

import java.io.IOException;
import java.util.stream.Collectors;

public class PartsPanel extends Panel<GuiEditor> implements IListCallback<PartsPanel.PartEntry> {
    private static final float Z_POSITION = 100f;
    private static final float PART_SCALE = 40f;

    private GuiList<PartEntry> partList;
    private GuiButton mountPointButton;
    private MountPoint mountPoint; // todo temporary until UI rework. Tabs with search?
    private float rotation;

    private final int listTop = 35;

    PartsPanel(GuiEditor parent, int left, int top, int right, int bottom) {
        super(parent, left, top, right, bottom);
        alwaysReceiveMouse = true;
        mountPoint = MountPoint.CHEST;
    }

    @Override
    public void initGui() {
        initPartList();

        buttonList.add(mountPointButton = new GuiButtonExt(0, width / 2 - 25, 16, 50, 16, I18n.format("tails.mountpoint." + mountPoint.name())));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        rotation += partialTicks;

        zLevel = -100;
        drawGradientRect(0, 0, width, listTop, 0xEA000000, 0xEA000000);
        drawGradientRect(0, listTop, width, height, GuiEditor.DARK_GREY, GuiEditor.DARK_GREY);
        zLevel = 0;
        GlStateManager.color(1f, 1f, 1f, 1f);
        drawCenteredString(fontRenderer, I18n.format("tails.gui.parts"), width / 2, 5, GuiEditor.TEXT_COLOUR);
        partList.drawScreen(mouseX, mouseY, partialTicks);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == mountPointButton.id) {
            if (mountPoint.ordinal() + 1 >= MountPoint.values().length) {
                mountPoint = MountPoint.values()[0];
            } else {
                mountPoint = MountPoint.values()[mountPoint.ordinal() + 1];
            }

            initPartList();

            mountPointButton.displayString = I18n.format("tails.mountpoint." + mountPoint.name());
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
    public void handleMouseInput() {
        partList.handleMouseInput();
    }

    @Override
    public boolean onEntrySelected(GuiList guiList, int index, PartEntry entry) {
        return true;
    }

    private void initPartList() {
        this.partList = new GuiList<>(this, width, height - listTop, listTop, height, 55,
                PartRegistry.getPartsByMountPoint(mountPoint).map(PartEntry::new).collect(Collectors.toList()));
        this.partList.width = width;
        selectDefaultListEntry();
    }

    void selectDefaultListEntry() {
        partList.setCurrentIndex(0);
    }

    private void renderPart(int x, int y, OutfitPart part) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, Z_POSITION);

        Part basePart = part.getPart();
        if (basePart == null) return;
        Model model = basePart.getModel();
        if (model != null) {
            GlStateManager.rotate(rotation, 0f, 1f, 0f);
            GlStateManager.scale(PART_SCALE, PART_SCALE, PART_SCALE);
            ((ClientProxy) Tails.proxy).partRenderer.render(part);
        } else {
            drawTexturedModalRect(x - 16, y - 16, 0, 0, 32, 32);
            // todo render loading circle
        }

        GlStateManager.popMatrix();
    }

    class PartEntry implements GuiListExtended.IGuiListEntry {
        private static final int ADD_X = 1;
        private static final int ADD_Y = 40;
        private static final int ADD_WIDTH = 10;
        private static final int ADD_HEIGHT = 10;
        private static final int ADD_COLOUR = 0xFF666666;

        final OutfitPart outfitPart;
        final Part part;

        PartEntry(Part part) {
            this.part = part;
            this.outfitPart = new OutfitPart(part);
        }

        @Override
        public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            boolean currentPart = partList.getCurrentIndex() == slotIndex;
            renderPart(right - 40, y + (slotHeight / 2), outfitPart);
            ClientUtils.drawStringMultiLine(fontRenderer, part.name, 5, y + 17, GuiEditor.TEXT_COLOUR);

            if (currentPart) {
                //Yeah its not nice but eh, works
                GlStateManager.pushMatrix();
                GlStateManager.translate(5, y + 27, 0);
                GlStateManager.scale(.6f, .6f, 1f);
                //zLevel = 100;
                fontRenderer.drawString(I18n.format("gui.author"), 0, 0, GuiEditor.TEXT_COLOUR);
                GlStateManager.translate(0, 10, 0);
                fontRenderer.drawString(TextFormatting.AQUA + part.author, 0, 0, GuiEditor.TEXT_COLOUR);
                GlStateManager.popMatrix();
                //zLevel = 0;
                // Draw "add" button
                GuiUtils.drawGradientRect(0, x + ADD_X, y + ADD_Y, x + ADD_X + ADD_WIDTH, y + ADD_Y + ADD_HEIGHT, ADD_COLOUR, ADD_COLOUR);
                fontRenderer.drawString("+", x + ADD_X + (ADD_WIDTH / 4), y + ADD_Y + (ADD_HEIGHT / 4), GuiEditor.TEXT_COLOUR);
                //GuiUtils.drawContinuousTexturedBox(new ResourceLocation("textures/gui/widgets.png"), x+ADD_X, y+ADD_Y, 0, 66, ADD_WIDTH, ADD_HEIGHT, 200, 20, 2, zLevel);
            }
        }

        @Override
        public void updatePosition(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_) {
        }

        @Override
        public boolean mousePressed(int index, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
            if (GuiBaseScreen.isMouseOver(relativeX, relativeY, ADD_X, ADD_Y, ADD_WIDTH, ADD_HEIGHT)) {
                parent.addOutfitPart(new OutfitPart(part));
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                return true;
            }
            return false;
        }

        @Override
        public void mouseReleased(int index, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
        }
    }
}
