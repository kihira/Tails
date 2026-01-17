package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Math;
import org.jspecify.annotations.Nullable;
import uk.kihira.tails.client.*;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.client.render.LayerPart;

import javax.annotation.Nonnull;
import java.util.Collections;

public class PartsListPanel extends Panel<OutfitEditScreen> implements IOutfitPartSelected
{
    private final ExtendedButton addPartTabButton;
    private final ExtendedButton editPartTabButton;
    private final ExtendedButton mountPointButton;
    private final PartsList<AddPartEntry> addPartsList;
    private final PartsList<EditPartEntry> editPartsList;
    private final int listTop = 38;

    private MountPoint mountPoint; // todo temporary until UI rework. Tabs with search?
    private float partRotation;

    PartsListPanel(OutfitEditScreen parent, int x, int y, int width, int height)
    {
        super(parent, x, y, width, height);
        this.mountPoint = MountPoint.CHEST;

        addChild(this.addPartTabButton = new ExtendedButton(this.getX(), this.getY() + 16, this.getWidth() / 2, 20,
                Component.translatable("tails.gui.parts.add"),
                this::onAddPartTabButtonPressed));

        addChild(this.editPartTabButton = new ExtendedButton(this.getX() + (this.getWidth() / 2), this.getY() + 16, this.getWidth() / 2, 20,
                Component.translatable("tails.gui.parts.edit"),
                this::onEditPartTabButtonPressed));

        this.addPartsList = new PartsList<>(this.parent.getMinecraft(), this.getWidth(), this.getHeight() - this.listTop - 22, this.getY() + this.listTop);
        this.addPartsList.setX(0);
        this.initAddPartList(this.mountPoint);
        addChild(this.addPartsList);

        this.editPartsList = new PartsList<>(this.parent.getMinecraft(), this.getWidth(), this.getHeight() - this.listTop - 22, this.getY() + this.listTop);
        this.editPartsList.setX(0);

        final int buttonWidth = 100;
        this.addChild(this.mountPointButton = new ExtendedButton(this.getX() + ((this.getWidth() - buttonWidth) / 2), this.getHeight() - 20, buttonWidth, 20,
                Component.translatable("tails.mountpoint." + mountPoint.name()),
                this::onMountPointButtonPressed));
    }

    private void onEditPartTabButtonPressed(Button button)
    {
        removeChild(this.addPartsList);
        addChild(this.editPartsList);
        this.mountPointButton.active = false;

        // Need to refresh the list in case parts have been added/removed
        this.initEditPartList();
    }

    private void onAddPartTabButtonPressed(Button button)
    {
        removeChild(this.editPartsList);
        addChild(this.addPartsList);
        this.mountPointButton.active = true;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        this.partRotation += partialTicks;

        graphics.fill(getX(), getY(), getRight(), getY() + this.listTop, OutfitEditScreen.SOFT_BLACK);
        graphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("tails.gui.parts"), this.getWidth() / 2, getY() + 5, OutfitEditScreen.TEXT_COLOUR);

        super.renderWidget(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput)
    {

    }

    private void onMountPointButtonPressed(GuiEventListener button)
    {
        // Move to next enum for MountPoint or back to 0 if at the end
        final int mountPointOrdinalNext = this.mountPoint.ordinal() + 1;
        final int mountPointOrdinal = mountPointOrdinalNext >= MountPoint.values().length ? 0 : mountPointOrdinalNext;

        this.mountPoint = MountPoint.values()[mountPointOrdinal];
        this.mountPointButton.setMessage(Component.translatable("tails.mountpoint." + mountPoint.name()));

        this.initAddPartList(this.mountPoint);
    }

    private void initAddPartList(MountPoint mountPoint)
    {
        this.addPartsList.replaceEntries(PartRegistry.getPartsByMountPoint(mountPoint).map(AddPartEntry::new).toList());
    }

    private void initEditPartList()
    {
        var existingSelectedPart = this.editPartsList.getSelected();
        this.editPartsList.replaceEntries(this.parent.getOutfit().getParts().stream().map(EditPartEntry::new).toList());

        if (existingSelectedPart != null)
        {
            // Reselect previously selected part if it still exists
            for (var entry : this.editPartsList.children())
            {
                if (entry.outfitPart == existingSelectedPart.outfitPart)
                {
                    this.editPartsList.setSelected(entry);
                    break;
                }
            }
        }
    }

    @Override
    protected int contentHeight()
    {
        return this.height;
    }

    @Override
    protected double scrollRate()
    {
        return 0;
    }

    @Override
    public void onOutfitPartSelected(@Nullable OutfitPart part)
    {
        this.initEditPartList();
    }

    public static class PartsList<E extends BasePartEntry<E>> extends ObjectSelectionList<E>
    {
        private static final int ITEM_HEIGHT = 55;

        PartsList(Minecraft minecraft, int listWidth, int height, int y)
        {
            super(minecraft, listWidth, height, y, ITEM_HEIGHT);
        }

        @Override
        protected void renderListBackground(GuiGraphics graphics)
        {
            graphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), OutfitEditScreen.DARK_GREY);
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
        {
            super.renderWidget(graphics, mouseX, mouseY, partialTicks);
        }

        @Override
        public int getRowWidth()
        {
            return this.width - 11;
        }

        @Override
        public int getRowLeft()
        {
            return this.getX() + 3;
        }

        @Override
        protected int scrollBarX()
        {
            return this.getRowRight() + 2;
        }
    }

    public abstract class BasePartEntry<E extends BasePartEntry<E>> extends ObjectSelectionList.Entry<E>
    {
        protected final OutfitPart outfitPart;
        protected final Part part;
        private final Outfit previewOutfit = new Outfit();

        BasePartEntry(OutfitPart outfitPart)
        {
            this.outfitPart = outfitPart;
            this.part = outfitPart.getPart();
            this.previewOutfit.addPart(this.outfitPart);
        }
        @Nonnull
        @Override
        public Component getNarration()
        {
            return Component.translatable("narrator.select", this.part.name);
        }

        @Override
        public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovering, float partialTicks)
        {
            final var font = Minecraft.getInstance().font;
            graphics.drawString(font, this.part.name, this.getContentX() + 3, this.getContentY() + 3, OutfitEditScreen.TEXT_COLOUR);

            graphics.fill(this.getContentXMiddle(), this.getContentY() + 14, this.getContentRight(), this.getContentBottom(), OutfitEditScreen.SOFT_BLACK);
            renderPart(this.outfitPart, graphics, this.getContentXMiddle(), this.getContentY() + 14, this.getContentRight(), this.getContentBottom(), partialTicks);
        }

        private void renderPart(OutfitPart part, GuiGraphics graphics, int x, int y, int right, int bottom, float partialTick)
        {
            var poseStack = graphics.pose().pushMatrix();

            var basePart = part.getPart();
            if (basePart == null) return;

            var model = basePart.getModel();
            if (model != null)
            {
                var player = Minecraft.getInstance().player;
                var dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
                var renderer = dispatcher.getRenderer(player);
                var renderState = (LivingEntityRenderState) renderer.createRenderState(player, partialTick);
                renderState.lightCoords = 15728880;
                renderState.shadowPieces.clear();
                renderState.outlineColor = 0;
                renderState.bodyRot = 0;
                renderState.yRot = 0;
                renderState.xRot = 0;
                renderState.isInvisible = true; // todo temp whilst we're still rendering a player?

                renderState.setRenderData(LayerPart.OUTFIT_KEY, this.previewOutfit);

                var position = new Vector3f(0f, 0f, 0f);
                switch (part.mountPoint)
                {
                    case HEAD -> position.y = 2f;
                    case CHEST -> position.y = 0.7f;
                }
                var rot = new Quaternionf().rotationXYZ(Math.toRadians(180f), Math.toRadians(PartsListPanel.this.partRotation), 0f);
                graphics.submitEntityRenderState(renderState, 20f, position, rot, null, x, y, right, bottom);

                //poseStack.rotateAround(Axis.YP.rotationDegrees(this.rotation), 0, 0, 0);
                //poseStack.scale(PART_SCALE, PART_SCALE, PART_SCALE);
            }
            else
            {
                //graphics.blitSprite(x - 16, y - 16, 0, 0, 32, 32, 0);
                // todo render loading circle
            }

            poseStack.popMatrix();
        }
    }

    public class AddPartEntry extends BasePartEntry<AddPartEntry>
    {
        private static final int ADD_X = 1;
        private static final int ADD_Y = 38;
        private static final int ADD_WIDTH = 12;
        private static final int ADD_HEIGHT = 12;
        private static final int ADD_COLOUR = 0xFF666666;

        AddPartEntry(Part part)
        {
            super(new OutfitPart(part));
        }

        @Override
        public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovering, float partialTicks)
        {
            super.renderContent(graphics, mouseX, mouseY, hovering, partialTicks);

            if (hovering || PartsListPanel.this.addPartsList.getSelected() == this)
            {
                final var font = Minecraft.getInstance().font;
                //Yeah its not nice but eh, works
                var stack = graphics.pose().pushMatrix();
                stack.translate(this.getX() + 5, this.getContentY() + 15);
                stack.scale(.6f, .6f);

                graphics.drawString(font, Component.translatable("gui.author"), 0, 0, OutfitEditScreen.TEXT_COLOUR);
                stack.translate(font.width(Component.translatable("gui.author")) + 2, 0);
                graphics.drawString(font,part.author, 0, 0, -1);
                stack.popMatrix();

                // Draw "add" button
                //graphics.fill(this.getContentX() + ADD_X, this.getContentY() + ADD_Y, this.getContentX() + ADD_X + ADD_WIDTH, this.getContentY() + ADD_Y + ADD_HEIGHT, ADD_COLOUR);
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("widget/button"), this.getContentX()+ADD_X, this.getContentY()+ADD_Y, ADD_WIDTH, ADD_HEIGHT);
                graphics.drawCenteredString(font, "+", this.getContentX() + ((ADD_X + ADD_WIDTH) / 2) + 1, this.getContentY() + ADD_Y + (ADD_HEIGHT / 4) - 1, OutfitEditScreen.TEXT_COLOUR);
            }
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean scrolling)
        {
            if (GuiBaseScreen.isMouseOver(event.x(), event.y(), getX() + ADD_X, getY() + ADD_Y, getX() + ADD_X + ADD_WIDTH, getY() + ADD_Y + ADD_HEIGHT))
            {
                parent.addOutfitPart(new OutfitPart(this.part));
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));

                return true;
            }
            return false;
        }
    }

    public class EditPartEntry extends BasePartEntry<EditPartEntry>
    {
        private static final int REMOVE_X = 1;
        private static final int REMOVE_Y = 38;
        private static final int REMOVE_WIDTH = 12;
        private static final int REMOVE_HEIGHT = 12;

        EditPartEntry(OutfitPart part)
        {
            super(part);
        }

        @Override
        public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovering, float partialTicks)
        {
            super.renderContent(graphics, mouseX, mouseY, hovering, partialTicks);

            // TODO draw tint boxes

            if (hovering || PartsListPanel.this.editPartsList.getSelected() == this)
            {
                final var font = Minecraft.getInstance().font;

                // Draw remove button
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Identifier.withDefaultNamespace("widget/button"), this.getContentX() + REMOVE_X, this.getContentY() + REMOVE_Y, REMOVE_WIDTH, REMOVE_HEIGHT);
                graphics.drawCenteredString(font, "-", this.getContentX() + ((REMOVE_X + REMOVE_WIDTH) / 2) + 1, this.getContentY() + REMOVE_Y + (REMOVE_HEIGHT / 4) - 1, OutfitEditScreen.TEXT_COLOUR);
            }
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean scrolling)
        {
            PartsListPanel.this.editPartsList.setSelected(this);
            parent.setActiveOutfitPart(this.outfitPart);

            if (GuiBaseScreen.isMouseOver(event.x(), event.y(), getX() + REMOVE_X, getY() + REMOVE_Y, getX() + REMOVE_X + REMOVE_WIDTH, getY() + REMOVE_Y + REMOVE_HEIGHT))
            {
                parent.removeOutfitPart(this.outfitPart);
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));

                return true;
            }
            return false;
        }
    }
}
