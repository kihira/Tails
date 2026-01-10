package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
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

public class PartsListPanel extends Panel<OutfitEditScreen>
{
    private MountPoint mountPoint; // todo temporary until UI rework. Tabs with search?

    private final ExtendedButton mountPointButton;
    private final PartsList partsList;
    private final OutfitEditScreen parent;
    private final int listTop = 38;

    PartsListPanel(OutfitEditScreen parent, int x, int y, int width, int height)
    {
        super(parent, x, y, width, height);
        this.parent = parent;
        this.mountPoint = MountPoint.CHEST;

        this.partsList = new PartsList(this.parent.getMinecraft(), this.getWidth(), this.getHeight() - this.listTop, this.getY() + this.listTop);
        this.partsList.setX(0);
        this.partsList.initPartList(this.mountPoint);
        addChild(this.partsList);

        final int buttonWidth = 100;
        this.addChild(this.mountPointButton = new ExtendedButton(this.getX() + ((this.getWidth() - buttonWidth) / 2), this.getY() + 15, buttonWidth, 20,
                Component.translatable("tails.mountpoint." + mountPoint.name()),
                this::onMountPointButtonPressed));
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
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

        this.partsList.initPartList(this.mountPoint);

        this.mountPointButton.setMessage(Component.translatable("tails.mountpoint." + mountPoint.name()));
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

    public class PartsList extends ObjectSelectionList<PartsListPanel.PartsList.PartEntry>
    {
        private static final int ITEM_HEIGHT = 55;
        private float rotation;

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
            this.rotation += partialTicks;
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

        private void initPartList(MountPoint mountPoint)
        {
            this.replaceEntries(PartRegistry.getPartsByMountPoint(mountPoint).map(PartEntry::new).toList());
        }

        public class PartEntry extends ObjectSelectionList.Entry<PartEntry>
        {
            private static final int ADD_X = 1;
            private static final int ADD_Y = 38;
            private static final int ADD_WIDTH = 12;
            private static final int ADD_HEIGHT = 12;
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
                final Font font = Minecraft.getInstance().font;
                graphics.drawString(font, this.part.name, this.getContentX()+3, this.getContentY()+3, -1);

                if (hovering || PartsList.this.getSelected() == this)
                {
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

                graphics.fill(this.getContentXMiddle(), this.getContentY()+14, this.getContentRight(), this.getContentBottom(), OutfitEditScreen.SOFT_BLACK); // TODO keep? useful mostly for debugging render area
                renderPart(this.outfitPart, graphics, this.getContentXMiddle(), this.getContentY()+14, this.getContentRight(), this.getContentBottom(), partialTicks);
            }

            @Override
            public boolean mouseClicked(MouseButtonEvent event, boolean scrolling)
            {
                if (GuiBaseScreen.isMouseOver(event.x(), event.y(), getX() + ADD_X, getY() + ADD_Y, getX() + ADD_X + ADD_WIDTH, getY() + ADD_Y + ADD_HEIGHT))
                {
                    parent.addOutfitPart(new OutfitPart(part));
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));

                    return true;
                }

                PartsList.this.setSelected(this);
                return false;
            }

            @Nonnull
            @Override
            public Component getNarration()
            {
                return Component.translatable("narrator.select", this.part.name);
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
                    renderState.isInvisible = true; // todo temp whilst we're still rendering a player?

                    // todo create OutfitBuilder?
                    var outfit = new Outfit();
                    outfit.addPart(part);
                    renderState.setRenderData(LayerPart.OUTFIT_KEY, outfit);

                    // TODO position and scale is _fine_ for tails but need it to work for other parts. maybe we just be lazy with
                    // switch for now based on mount point
                    Vector3f position = new Vector3f(0f, 0f, 0f);
                    switch (part.mountPoint)
                    {
                        case HEAD -> position.y = 2f;
                        case CHEST -> position.y = 0.7f;
                    }
                    var rot = new Quaternionf().rotationXYZ(Math.toRadians(180f), Math.toRadians(rotation), 0f);
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
    }
}
