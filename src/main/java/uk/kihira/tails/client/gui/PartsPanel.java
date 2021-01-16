package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractList;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import uk.kihira.gltf.Model;
import uk.kihira.tails.client.*;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.proxy.ClientProxy;

import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class PartsPanel extends Panel<GuiEditor> implements IListCallback<PartsPanel.PartEntry>
{
    private static final float Z_POSITION = 100f;
    private static final float PART_SCALE = 40f;

    private GuiList<PartsPanel.PartEntry> partList;
    private ExtendedButton mountPointButton;
    private MountPoint mountPoint; // todo temporary until UI rework. Tabs with search?
    private float rotation;

    private final int listTop = 35;

    PartsPanel(GuiEditor parent, int left, int top, int right, int bottom)
    {
        super(parent, left, top, right, bottom);
        this.alwaysReceiveMouse = true;
        this.mountPoint = MountPoint.CHEST;
    }

    @Override
    public void init()
    {
        initPartList();

        addButton(this.mountPointButton = new ExtendedButton(
                this.width / 2 - 25,
                16,
                50,
                16,
                new TranslationTextComponent("tails.mountpoint." + mountPoint.name()),
                this::onMountPointButtonPressed)
        );

        super.init();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.rotation += partialTicks;

        GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(),  -100,0, 0, this.width, this.listTop, GuiEditor.SOFT_BLACK, GuiEditor.SOFT_BLACK);
        GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(),  -100,0, this.listTop, this.width, this.height, GuiEditor.DARK_GREY, GuiEditor.DARK_GREY);

        RenderSystem.color4f(1f, 1f, 1f, 1f);
        drawCenteredString(matrixStack, this.font, I18n.format("tails.gui.parts"), this.width / 2, 5, GuiEditor.TEXT_COLOUR);
        this.partList.render(matrixStack, mouseX, mouseY, partialTicks);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void onMountPointButtonPressed(Button button)
    {
        // Move to next enum for MointPoint or back to 0 if at the end
        final int mountPointOrdinalNext = this.mountPoint.ordinal() + 1;
        final int mountPointOrdinal = mountPointOrdinalNext >= MountPoint.values().length ? 0 : mountPointOrdinalNext;

        this.mountPoint = MountPoint.values()[mountPointOrdinal];

        initPartList();

        this.mountPointButton.setMessage(new TranslationTextComponent("tails.mountpoint." + mountPoint.name()));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        partList.mouseClicked(mouseX, mouseY, mouseButton);
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int state)
    {
        partList.mouseReleased(mouseX, mouseY, state);
        return super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public boolean onEntrySelected(GuiList<PartEntry> guiList, int index, PartEntry entry)
    {
        return true;
    }

    private void initPartList()
    {
        this.partList = new GuiList<>(
                this,
                this.width,
                this.height - this.listTop,
                this.listTop,
                this.height,
                55,
                PartRegistry.getPartsByMountPoint(this.mountPoint).map(PartEntry::new).collect(Collectors.toList()));
        // this.partList.width = width;
        selectDefaultListEntry();
    }

    void selectDefaultListEntry()
    {
        this.partList.setDefault();
    }

    private void renderPart(MatrixStack matrixStack, int x, int y, OutfitPart part)
    {
        matrixStack.push();
        matrixStack.translate(x, y, Z_POSITION);

        Part basePart = part.getPart();
        if (basePart == null) return;

        Model model = basePart.getModel();
        if (model != null)
        {
            matrixStack.rotate(Vector3f.YP.rotationDegrees(this.rotation));
            matrixStack.scale(PART_SCALE, PART_SCALE, PART_SCALE);
            ((ClientProxy) Tails.proxy).partRenderer.render(matrixStack, part);
        }
        else
        {
            GuiUtils.drawTexturedModalRect(matrixStack, x - 16, y - 16, 0, 0, 32, 32, 0);
            // todo render loading circle
        }

        matrixStack.pop();
    }

    @OnlyIn(Dist.CLIENT)
    class PartEntry extends AbstractList.AbstractListEntry<PartEntry>
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
        public void render(MatrixStack matrixStack, int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks)
        {
            final boolean isCurrentSelectedPart = this == partList.getSelected();

            renderPart(matrixStack, right - 40, y + (slotHeight / 2), this.outfitPart);
            ClientUtils.drawStringMultiLine(matrixStack, font, this.part.name, 5, y + 17, GuiEditor.TEXT_COLOUR);

            if (isCurrentSelectedPart)
            {
                //Yeah its not nice but eh, works
                matrixStack.push();
                matrixStack.translate(5, y + 27, 0);
                matrixStack.scale(.6f, .6f, 1f);

                font.drawString(matrixStack, I18n.format("gui.author"), 0, 0, GuiEditor.TEXT_COLOUR);
                matrixStack.translate(0, 10, 0);
                font.drawString(matrixStack, TextFormatting.AQUA + part.author, 0, 0, GuiEditor.TEXT_COLOUR);
                matrixStack.pop();

                // Draw "add" button
                GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), 0, x + ADD_X, y + ADD_Y, x + ADD_X + ADD_WIDTH, y + ADD_Y + ADD_HEIGHT, ADD_COLOUR, ADD_COLOUR);
                font.drawString(matrixStack, "+", x + ADD_X + (ADD_WIDTH / 4), y + ADD_Y + (ADD_HEIGHT / 4), GuiEditor.TEXT_COLOUR);
                //GuiUtils.drawContinuousTexturedBox(new ResourceLocation("textures/gui/widgets.png"), x+ADD_X, y+ADD_Y, 0, 66, ADD_WIDTH, ADD_HEIGHT, 200, 20, 2, zLevel);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            if (GuiBaseScreen.isMouseOver(mouseX, mouseY, ADD_X, ADD_Y, ADD_WIDTH, ADD_HEIGHT))
            {
                parent.addOutfitPart(new OutfitPart(part));
                getMinecraft().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1f));

                return true;
            }
            return false;
        }
    }
}
