/*
package uk.kihira.tails.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import uk.kihira.tails.client.Colour;
import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.client.gui.controls.NumberInput;

import javax.annotation.Nullable;

public class TransformPanel extends Panel<GuiEditor> implements IControlCallback<IControl<Float>, Float>, IOutfitPartSelected
{
    private static final float MAX_ROTATION = 180;
    private static final float MIN_ROTATION = -180;
    private static final float INC_ROTATION = 1.f;

    private static final float MAX_POSITION = 2;
    private static final float MIN_POSITION = -2;
    private static final float INC_POSITION = .1f;

    private static final float MAX_SCALE = 2;
    private static final float MIN_SCALE = .5f;
    private static final float INC_SCALE = .1f;

    private static final int WIDTH = 45;

    private final int spacing = 15;

    private NumberInput xRotInput;
    private NumberInput yRotInput;
    private NumberInput zRotInput;
    
    private NumberInput xPosInput;
    private NumberInput yPosInput;
    private NumberInput zPosInput;
    
    private NumberInput xScaleInput;
    private NumberInput yScaleInput;
    private NumberInput zScaleInput;

    private ExtendedButton mountPointButton;

    TransformPanel(GuiEditor parent, int x, int y, int width, int height)
    {
        super(parent, x, y, width, height);
    }

    @Override
    public void init()
    {
        final int firstInputX = 3;
        final int secondInputX = 52;
        final int thirdInputX = 101;

        addRenderableWidget(xRotInput = new NumberInput(firstInputX, spacing, WIDTH, MIN_ROTATION, MAX_ROTATION, INC_ROTATION, this));
        addRenderableWidget(yRotInput = new NumberInput(secondInputX, spacing, WIDTH, MIN_ROTATION, MAX_ROTATION, INC_ROTATION, this));
        addRenderableWidget(zRotInput = new NumberInput(thirdInputX, spacing, WIDTH, MIN_ROTATION, MAX_ROTATION, INC_ROTATION, this));

        addRenderableWidget(xPosInput = new NumberInput(firstInputX, spacing * 3, WIDTH, MIN_POSITION, MAX_POSITION, INC_POSITION, this));
        addRenderableWidget(yPosInput = new NumberInput(secondInputX, spacing * 3, WIDTH, MIN_POSITION, MAX_POSITION, INC_POSITION, this));
        addRenderableWidget(zPosInput = new NumberInput(thirdInputX, spacing * 3, WIDTH, MIN_POSITION, MAX_POSITION, INC_POSITION, this));

        addRenderableWidget(xScaleInput = new NumberInput(firstInputX, spacing * 5, WIDTH, MIN_SCALE, MAX_SCALE, INC_SCALE, this));
        addRenderableWidget(yScaleInput = new NumberInput(secondInputX, spacing * 5, WIDTH, MIN_SCALE, MAX_SCALE, INC_SCALE, this));
        addRenderableWidget(zScaleInput = new NumberInput(thirdInputX, spacing * 5, WIDTH, MIN_SCALE, MAX_SCALE, INC_SCALE, this));

        String mountPoint = MountPoint.values()[0].name();
        final OutfitPart outfitPart = parent.getCurrentOutfitPart();
        if (outfitPart != null) mountPoint = outfitPart.mountPoint.name();

        this.addRenderableWidget(mountPointButton = new ExtendedButton(5, spacing * 7, width - 10, 20, Component.translatable("tails.mountpoint." + mountPoint), this::onChangeMountPointButtonPressed));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        graphics.hLine(0, width, 0, Colour.BLACK);

        graphics.fillGradient(-100,0, 0, width, height, GuiEditor.DARK_GREY, GuiEditor.DARK_GREY);

        // Rotation
        graphics.drawString(this.font, Component.translatable("tails.gui.rotation"), 5, this.font.lineHeight / 2, GuiEditor.TEXT_COLOUR);
        // Position
        graphics.drawString(this.font, Component.translatable("tails.gui.position"), 5, spacing * 2 + this.font.lineHeight / 2, GuiEditor.TEXT_COLOUR);
        // Scale
        graphics.drawString(this.font, Component.translatable("tails.gui.scale"), 5, spacing * 4 + this.font.lineHeight / 2, GuiEditor.TEXT_COLOUR);
        // Mount point
        graphics.drawString(this.font, Component.translatable("tails.gui.mountpoint"), 5, spacing * 6 + this.font.lineHeight / 2, GuiEditor.TEXT_COLOUR);

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    protected void onChangeMountPointButtonPressed(GuiEventListener button)
    {
        final OutfitPart outfitPart = parent.getCurrentOutfitPart();
        if (outfitPart == null) return;

        // Move to next enum for MointPoint or back to 0 if at the end
        final int mountPointOrdinalNext = outfitPart.mountPoint.ordinal() + 1;
        final int mountPointOrdinal = mountPointOrdinalNext >= MountPoint.values().length ? 0 : mountPointOrdinalNext;

        outfitPart.mountPoint = MountPoint.values()[mountPointOrdinal];

        mountPointButton.setMessage(Component.translatable("tails.mountpoint." + outfitPart.mountPoint.name()));
    }

    @Override
    public boolean onValueChange(IControl<Float> control, Float oldValue, Float newValue)
    {
        final OutfitPart outfitPart = parent.getCurrentOutfitPart();
        if (outfitPart == null) return true;

        // Rot
        if (control == xRotInput) outfitPart.rotation[0] = newValue;
        else if (control == yRotInput) outfitPart.rotation[1] = newValue;
        else if (control == zRotInput) outfitPart.rotation[2] = newValue;
        // Pos
        else if (control == xPosInput) outfitPart.mountOffset[0] = newValue;
        else if (control == yPosInput) outfitPart.mountOffset[1] = newValue;
        else if (control == zPosInput) outfitPart.mountOffset[2] = newValue;
        // Scale
        else if (control == xScaleInput) outfitPart.scale[0] = newValue;
        else if (control == yScaleInput) outfitPart.scale[1] = newValue;
        else if (control == zScaleInput) outfitPart.scale[2] = newValue;

        return true;
    }

    @Override
    public void OnOutfitPartSelected(@Nullable OutfitPart part)
    {
        if (part == null) return;

        xPosInput.setValue(part.mountOffset[0]);
        yPosInput.setValue(part.mountOffset[1]);
        zPosInput.setValue(part.mountOffset[2]);

        xRotInput.setValue(part.rotation[0]);
        yRotInput.setValue(part.rotation[1]);
        zRotInput.setValue(part.rotation[2]);

        xScaleInput.setValue(part.scale[0]);
        yScaleInput.setValue(part.scale[1]);
        zScaleInput.setValue(part.scale[2]);
    }
}
*/
