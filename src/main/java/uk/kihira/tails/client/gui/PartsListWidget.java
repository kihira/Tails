package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.client.gui.widget.ModListWidget;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import uk.kihira.tails.client.*;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.common.Tails;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
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
    protected int getScrollbarPosition()
    {
        return this.listWidth;
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
        var poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(x, y, Z_POSITION);
        poseStack.mulPoseMatrix(new Matrix4f().scaling(PART_SCALE, PART_SCALE, -PART_SCALE));
        poseStack.translate(0, -1.5, 0);

        var basePart = part.getPart();
        if (basePart == null) return;

        var model = basePart.getModel();
        if (model != null)
        {
            //poseStack.rotateAround(Axis.YP.rotationDegrees(this.rotation), 0, 0, 0);
            //poseStack.scale(PART_SCALE, PART_SCALE, PART_SCALE);

            Lighting.setupForEntityInInventory();
            model.setupAnim(minecraft.player, 0, 0, partialTick, 0, 0, 0);
            model.renderToBuffer(poseStack, graphics.bufferSource().getBuffer(RenderType.entityCutoutNoCull(part.textureLoc)), MAGIC_PACKED_LIGHT, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
            Lighting.setupFor3DItems();
        }
        else
        {
            //graphics.blitSprite(x - 16, y - 16, 0, 0, 32, 32, 0);
            // todo render loading circle
        }

        poseStack.popPose();
    }

    @Override
    public boolean onEntrySelected(GuiList<PartEntry> guiList, int index, PartEntry entry)
    {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
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
        public void render(GuiGraphics graphics, int slotIndex, int top, int left, int listWidth, int slotHeight, int mouseX, int mouseY, boolean hovering, float partialTicks)
        {
            graphics.enableScissor(top, left, top + slotHeight, left + listWidth);
            renderPart(this.outfitPart, graphics, left + listWidth - 30, top + (slotHeight / 2), partialTicks);
            graphics.disableScissor();
            graphics.drawString(minecraft.font, this.part.name, left+5, top+17, GuiEditor.TEXT_COLOUR);

            if (hovering)
            {
                //Yeah its not nice but eh, works
                graphics.pose().pushPose();
                graphics.pose().translate(5, top + 27, 0);
                graphics.pose().scale(.6f, .6f, 1f);

                graphics.drawString(minecraft.font, Component.translatable("gui.author"), 0, 0, GuiEditor.TEXT_COLOUR);
                graphics.pose().translate(minecraft.font.width(Component.translatable("gui.author"))+2, 0, 0);
                graphics.drawString(minecraft.font,part.author, 0, 0, GuiEditor.TEXT_COLOUR);
                graphics.pose().popPose();

                // Draw "add" button
                graphics.fill(left + ADD_X, top + ADD_Y, left + ADD_X + ADD_WIDTH, top + ADD_Y + ADD_HEIGHT, ADD_COLOUR);
                graphics.drawString(minecraft.font, "+", left + ADD_X + (ADD_WIDTH / 4), top + ADD_Y + (ADD_HEIGHT / 4), GuiEditor.TEXT_COLOUR);
                graphics.blitSprite(new ResourceLocation("widget/button"), left+ADD_X, top+ADD_Y, ADD_WIDTH, ADD_HEIGHT);
            }
        }

        @Override
        public void renderBack(GuiGraphics graphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick)
        {
            graphics.fillGradient(left, top, left + listWidth, top + height, GuiEditor.SOFT_BLACK, GuiEditor.SOFT_BLACK);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            if (GuiBaseScreen.isMouseOver(mouseX, mouseY, getX() + ADD_X, getY() + ADD_Y, getX() + ADD_X + ADD_WIDTH, getY() + ADD_Y + ADD_HEIGHT))
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
