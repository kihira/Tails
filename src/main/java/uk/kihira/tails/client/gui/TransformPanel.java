package uk.kihira.tails.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import uk.kihira.tails.client.Colour;
import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.client.gui.controls.NumberInput;

import javax.annotation.Nullable;

public class TransformPanel extends Panel<OutfitEditScreen> implements IControlCallback<IControl<Float>, Float>, IOutfitPartSelected
{
    private static final float MAX_ROTATION = 180.f;
    private static final float MIN_ROTATION = -180.f;
    private static final float INC_ROTATION = 1.f;

    private static final float MAX_POSITION = 2.f;
    private static final float MIN_POSITION = -2.f;
    private static final float INC_POSITION = .1f;

    private static final float MAX_SCALE = 2.f;
    private static final float MIN_SCALE = .5f;
    private static final float INC_SCALE = .1f;

    private static final int WIDTH = 47;

    private final int spacing = 15;

    private final NumberInput xRotInput;
    private final NumberInput yRotInput;
    private final NumberInput zRotInput;
    
    private final NumberInput xPosInput;
    private final NumberInput yPosInput;
    private final NumberInput zPosInput;
    
    private final NumberInput xScaleInput;
    private final NumberInput yScaleInput;
    private final NumberInput zScaleInput;

    private final ExtendedButton mountPointButton;

    TransformPanel(OutfitEditScreen parent, int x, int y, int width, int height)
    {
        super(parent, x, y, width, height);

        final int xSpacing = 1;
        final int firstInputX = 4 + xSpacing;
        final int secondInputX = firstInputX + WIDTH + xSpacing;
        final int thirdInputX = secondInputX + WIDTH + xSpacing;

        final int maxIntPlacesRot = 3, maxDecimalPlacesRot = 1;
        final int maxIntPlacesPos = 1, maxDecimalPlacesPos = 2;
        final int maxIntPlacesScale = 1, maxDecimalPlacesScale = 2;
        addChild(xRotInput = new NumberInput(this.getX() + firstInputX, this.getY() + spacing, WIDTH, MIN_ROTATION, MAX_ROTATION, INC_ROTATION, maxIntPlacesRot, maxDecimalPlacesRot, this));
        addChild(yRotInput = new NumberInput(this.getX() + secondInputX, this.getY() + spacing, WIDTH, MIN_ROTATION, MAX_ROTATION, INC_ROTATION, maxIntPlacesRot, maxDecimalPlacesRot, this));
        addChild(zRotInput = new NumberInput(this.getX() + thirdInputX, this.getY() + spacing, WIDTH, MIN_ROTATION, MAX_ROTATION, INC_ROTATION, maxIntPlacesRot, maxDecimalPlacesRot, this));

        addChild(xPosInput = new NumberInput(this.getX() + firstInputX, this.getY() + spacing * 3, WIDTH, MIN_POSITION, MAX_POSITION, INC_POSITION, maxIntPlacesPos, maxDecimalPlacesPos, this));
        addChild(yPosInput = new NumberInput(this.getX() + secondInputX, this.getY() + spacing * 3, WIDTH, MIN_POSITION, MAX_POSITION, INC_POSITION, maxIntPlacesPos, maxDecimalPlacesPos, this));
        addChild(zPosInput = new NumberInput(this.getX() + thirdInputX, this.getY() + spacing * 3, WIDTH, MIN_POSITION, MAX_POSITION, INC_POSITION, maxIntPlacesPos, maxDecimalPlacesPos, this));

        addChild(xScaleInput = new NumberInput(this.getX() + firstInputX, this.getY() + spacing * 5, WIDTH, MIN_SCALE, MAX_SCALE, INC_SCALE, maxIntPlacesScale, maxDecimalPlacesScale, this));
        addChild(yScaleInput = new NumberInput(this.getX() + secondInputX, this.getY() + spacing * 5, WIDTH, MIN_SCALE, MAX_SCALE, INC_SCALE, maxIntPlacesScale, maxDecimalPlacesScale, this));
        addChild(zScaleInput = new NumberInput(this.getX() + thirdInputX, this.getY() + spacing * 5, WIDTH, MIN_SCALE, MAX_SCALE, INC_SCALE, maxIntPlacesScale, maxDecimalPlacesScale, this));

        String mountPoint = MountPoint.values()[0].name();
        final OutfitPart outfitPart = parent.getCurrentOutfitPart();
        if (outfitPart != null) mountPoint = outfitPart.mountPoint.name();

        addChild(mountPointButton = new ExtendedButton( this.getX() + 5,  this.getY() + spacing * 7, width - 10, 20, Component.translatable("tails.mountpoint." + mountPoint), this::onChangeMountPointButtonPressed));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        graphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), OutfitEditScreen.DARK_GREY);
        graphics.hLine(this.getX(), this.getRight(), this.getY(), Colour.BLACK);

        // Rotation
        graphics.drawString(this.font(), Component.translatable("tails.gui.rotation"),  this.getX() + 5, this.getY() + this.font().lineHeight / 2, OutfitEditScreen.TEXT_COLOUR);
        // Position
        graphics.drawString(this.font(), Component.translatable("tails.gui.position"), this.getX() + 5, this.getY() + spacing * 2 + this.font().lineHeight / 2, OutfitEditScreen.TEXT_COLOUR);
        // Scale
        graphics.drawString(this.font(), Component.translatable("tails.gui.scale"), this.getX() + 5, this.getY() + spacing * 4 + this.font().lineHeight / 2, OutfitEditScreen.TEXT_COLOUR);
        // Mount point
        graphics.drawString(this.font(), Component.translatable("tails.gui.mountpoint"), this.getX() + 5, this.getY() + spacing * 6 + this.font().lineHeight / 2, OutfitEditScreen.TEXT_COLOUR);

        super.renderWidget(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    protected void onChangeMountPointButtonPressed(GuiEventListener button)
    {
        final OutfitPart outfitPart = this.parent.getCurrentOutfitPart();
        if (outfitPart == null) return;

        // Move to next enum for MountPoint or back to 0 if at the end
        final int mountPointOrdinalNext = outfitPart.mountPoint.ordinal() + 1;
        final int mountPointOrdinal = mountPointOrdinalNext >= MountPoint.values().length ? 0 : mountPointOrdinalNext;

        outfitPart.mountPoint = MountPoint.values()[mountPointOrdinal];

        this.mountPointButton.setMessage(Component.translatable("tails.mountpoint." + outfitPart.mountPoint.name()));
    }

    @Override
    public boolean onValueChange(IControl<Float> control, Float oldValue, Float newValue)
    {
        final OutfitPart outfitPart = this.parent.getCurrentOutfitPart();
        if (outfitPart == null) return true;

        // Rot
        if (control == this.xRotInput) outfitPart.rotation[0] = newValue;
        else if (control == this.yRotInput) outfitPart.rotation[1] = newValue;
        else if (control == this.zRotInput) outfitPart.rotation[2] = newValue;
        // Pos
        else if (control == this.xPosInput) outfitPart.mountOffset[0] = newValue;
        else if (control == this.yPosInput) outfitPart.mountOffset[1] = newValue;
        else if (control == this.zPosInput) outfitPart.mountOffset[2] = newValue;
        // Scale
        else if (control == this.xScaleInput) outfitPart.scale[0] = newValue;
        else if (control == this.yScaleInput) outfitPart.scale[1] = newValue;
        else if (control == this.zScaleInput) outfitPart.scale[2] = newValue;

        return true;
    }

    @Override
    public void onOutfitPartSelected(@Nullable OutfitPart part)
    {
        if (part == null)
        {
            return;
        }

        this.xPosInput.setValue(part.mountOffset[0]);
        this.yPosInput.setValue(part.mountOffset[1]);
        this.zPosInput.setValue(part.mountOffset[2]);

        this.xRotInput.setValue(part.rotation[0]);
        this.yRotInput.setValue(part.rotation[1]);
        this.zRotInput.setValue(part.rotation[2]);

        this.xScaleInput.setValue(part.scale[0]);
        this.yScaleInput.setValue(part.scale[1]);
        this.zScaleInput.setValue(part.scale[2]);
    }

    @Override
    protected int contentHeight()
    {
        //todo
        return 0;
    }

    @Override
    protected double scrollRate()
    {
        //todo
        return 0;
    }
}
