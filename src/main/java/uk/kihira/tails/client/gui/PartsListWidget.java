package uk.kihira.tails.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import uk.kihira.tails.client.*;
import uk.kihira.tails.client.outfit.OutfitPart;

import javax.annotation.Nonnull;

public class PartsListWidget extends ObjectSelectionList<PartsListWidget.PartEntry> implements IListCallback<PartsListWidget.PartEntry>
{
    private static final float Z_POSITION = 100f;
    private static final float PART_SCALE = 40f;
    private static final int ITEM_HEIGHT = 55;
    private static final int MAGIC_PACKED_LIGHT = 15728640; // Want to do something properly here, will be fine for now
    private final int listWidth;

    private ExtendedButton mountPointButton;
    private MountPoint mountPoint; // todo temporary until UI rework. Tabs with search?
    private float rotation;

    private final GuiEditor parent;
    private final int listTop = 35;

    PartsListWidget(GuiEditor parent, int listWidth, int height, int top)
    {
        super(parent.getMinecraft(), listWidth, height, top, ITEM_HEIGHT);
        this.parent = parent;
        this.mountPoint = MountPoint.CHEST;
        this.listWidth = listWidth;

        //setRenderBackground(false);
        initPartList();
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        this.rotation += partialTicks;

        //graphics.fillGradient(-100,0, 0, this.width, this.listTop, GuiEditor.SOFT_BLACK, GuiEditor.SOFT_BLACK);
        //graphics.fillGradient(getX(), getY(), getRight(), getBottom(), GuiEditor.DARK_GREY, GuiEditor.DARK_GREY);

        //graphics.drawCenteredString(this.minecraft.font, Component.translatable("tails.gui.parts"), this.width / 2, 5, GuiEditor.TEXT_COLOUR);

        super.renderWidget(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public int getRowWidth()
    {
        return this.listWidth;
    }

    private void onMountPointButtonPressed(GuiEventListener button)
    {
        // Move to next enum for MointPoint or back to 0 if at the end
        final int mountPointOrdinalNext = this.mountPoint.ordinal() + 1;
        final int mountPointOrdinal = mountPointOrdinalNext >= MountPoint.values().length ? 0 : mountPointOrdinalNext;

        this.mountPoint = MountPoint.values()[mountPointOrdinal];

        initPartList();

        this.mountPointButton.setMessage(Component.translatable("tails.mountpoint." + mountPoint.name()));
    }

    private void initPartList()
    {
        this.clearEntries();
        PartRegistry.getPartsByMountPoint(this.mountPoint).map(PartEntry::new).forEach(this::addEntry);
    }

    private void renderPart(OutfitPart part, GuiGraphics graphics, int x, int y, float partialTick)
    {
        var poseStack = graphics.pose().pushMatrix();
        poseStack.translate(x, y)
                .scaling(PART_SCALE, PART_SCALE)
                .translate(0.f, -1.5f);

        var basePart = part.getPart();
        if (basePart == null) return;

        var model = basePart.getModel();
        if (model != null)
        {
            //poseStack.rotateAround(Axis.YP.rotationDegrees(this.rotation), 0, 0, 0);
            //poseStack.scale(PART_SCALE, PART_SCALE, PART_SCALE);

            //Lighting.setupForEntityInInventory();
            //model.setupAnim(minecraft.player, 0, 0, partialTick, 0, 0, 0);
            //model.renderToBuffer(poseStack, graphics.bufferSource().getBuffer(RenderTypes.entityCutoutNoCull(part.textureLoc)), MAGIC_PACKED_LIGHT, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
            //Lighting.setupFor3DItems();
        }
        else
        {
            //graphics.blitSprite(x - 16, y - 16, 0, 0, 32, 32, 0);
            // todo render loading circle
        }

        poseStack.popMatrix();
    }

    @Override
    public boolean onEntrySelected(GuiList<PartEntry> guiList, int index, PartEntry entry)
    {
        return true;
    }

    public class PartEntry extends ObjectSelectionList.Entry<PartEntry>
    {
        private static final int ADD_X = 1;
        private static final int ADD_Y = 40;
        private static final int ADD_WIDTH = 10;
        private static final int ADD_HEIGHT = 10;
        private static final int ADD_COLOUR = 0xFF666666;

        final OutfitPart outfitPart;
        final Part part;

        PartEntry(Part part)
        {
            this.part = part;
            this.outfitPart = new OutfitPart(part);
        }

        @Override
        public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovering, float partialTicks)
        {
            graphics.fillGradient(this.getContentX(), this.getContentY(), this.getContentRight(), this.getContentHeight(), GuiEditor.SOFT_BLACK, GuiEditor.SOFT_BLACK);

            graphics.enableScissor(this.getContentY(), this.getContentX(), this.getContentHeight(), this.getContentRight());
            renderPart(this.outfitPart, graphics, this.getContentRight() - 30, this.getContentYMiddle(), partialTicks);
            graphics.disableScissor();
            graphics.drawString(minecraft.font, this.part.name, this.getContentX()+5, this.getContentY()+17, GuiEditor.TEXT_COLOUR);

            if (hovering)
            {
                //Yeah its not nice but eh, works
                var stack = graphics.pose().pushMatrix();
                stack.translate(5, this.getContentY() + 27);
                stack.scale(.6f, .6f);

                graphics.drawString(minecraft.font, Component.translatable("gui.author"), 0, 0, GuiEditor.TEXT_COLOUR);
                stack.translate(minecraft.font.width(Component.translatable("gui.author"))+2, 0);
                graphics.drawString(minecraft.font,part.author, 0, 0, GuiEditor.TEXT_COLOUR);
                stack.popMatrix();

                // Draw "add" button
                graphics.fill(this.getContentX() + ADD_X, this.getContentY() + ADD_Y, this.getContentX() + ADD_X + ADD_WIDTH, this.getContentY() + ADD_Y + ADD_HEIGHT, ADD_COLOUR);
                graphics.drawString(minecraft.font, "+", this.getContentX() + ADD_X + (ADD_WIDTH / 4), this.getContentY() + ADD_Y + (ADD_HEIGHT / 4), GuiEditor.TEXT_COLOUR);
                //graphics.blitSprite(Identifier.withDefaultNamespace("widget/button"), this.getContentX()+ADD_X, this.getContentY()+ADD_Y, ADD_WIDTH, ADD_HEIGHT);
            }
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean scrolling)
        {
            if (GuiBaseScreen.isMouseOver(event.x(), event.y(), getX() + ADD_X, getY() + ADD_Y, getX() + ADD_X + ADD_WIDTH, getY() + ADD_Y + ADD_HEIGHT))
            {
                parent.addOutfitPart(new OutfitPart(part));
                minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));

                return true;
            }

            PartsListWidget.this.setSelected(this);
            return false;
        }

        @Nonnull
        @Override
        public Component getNarration()
        {
            return Component.translatable("narrator.select", this.part.name);
        }
    }
}
