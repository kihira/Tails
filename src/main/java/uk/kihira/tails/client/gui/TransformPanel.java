package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import uk.kihira.tails.client.Colour;
import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.client.gui.controls.NumberInput;

import javax.annotation.Nullable;
import java.io.IOException;

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

        xRotInput = new NumberInput(firstInputX, spacing, WIDTH, MIN_ROTATION, MAX_ROTATION, INC_ROTATION, this);
        yRotInput = new NumberInput(secondInputX, spacing, WIDTH, MIN_ROTATION, MAX_ROTATION, INC_ROTATION, this);
        zRotInput = new NumberInput(thirdInputX, spacing, WIDTH, MIN_ROTATION, MAX_ROTATION, INC_ROTATION, this);

        xPosInput = new NumberInput(firstInputX, spacing * 3, WIDTH, MIN_POSITION, MAX_POSITION, INC_POSITION, this);
        yPosInput = new NumberInput(secondInputX, spacing * 3, WIDTH, MIN_POSITION, MAX_POSITION, INC_POSITION, this);
        zPosInput = new NumberInput(thirdInputX, spacing * 3, WIDTH, MIN_POSITION, MAX_POSITION, INC_POSITION, this);

        xScaleInput = new NumberInput(firstInputX, spacing * 5, WIDTH, MIN_SCALE, MAX_SCALE, INC_SCALE, this);
        yScaleInput = new NumberInput(secondInputX, spacing * 5, WIDTH, MIN_SCALE, MAX_SCALE, INC_SCALE, this);
        zScaleInput = new NumberInput(thirdInputX, spacing * 5, WIDTH, MIN_SCALE, MAX_SCALE, INC_SCALE, this);

        String mountPoint = MountPoint.values()[0].name();
        final OutfitPart outfitPart = parent.getCurrentOutfitPart();
        if (outfitPart != null) mountPoint = outfitPart.mountPoint.name();

        this.addButton(mountPointButton = new ExtendedButton(5, spacing * 7, width - 10, 20, new TranslationTextComponent("tails.mountpoint." + mountPoint), this::onChangeMountPointButtonPressed));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        this.hLine(matrixStack, 0, width, 0, Colour.BLACK);

        GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), -100,0, 0, width, height, GuiEditor.DARK_GREY, GuiEditor.DARK_GREY);

        // Rotation
        this.font.drawString(matrixStack, I18n.format("tails.gui.rotation"), 5, this.font.FONT_HEIGHT / 2f, GuiEditor.TEXT_COLOUR);
        xRotInput.draw(mouseX, mouseY);
        yRotInput.draw(mouseX, mouseY);
        zRotInput.draw(mouseX, mouseY);

        // Position
        this.font.drawString(matrixStack, I18n.format("tails.gui.position"), 5, spacing * 2 + this.font.FONT_HEIGHT / 2f, GuiEditor.TEXT_COLOUR);
        xPosInput.draw(mouseX, mouseY);
        yPosInput.draw(mouseX, mouseY);
        zPosInput.draw(mouseX, mouseY);

        // Scale
        this.font.drawString(matrixStack, I18n.format("tails.gui.scale"), 5, spacing * 4 + this.font.FONT_HEIGHT / 2f, GuiEditor.TEXT_COLOUR);
        xScaleInput.draw(mouseX, mouseY);
        yScaleInput.draw(mouseX, mouseY);
        zScaleInput.draw(mouseX, mouseY);

        // Mount point
        this.font.drawString(matrixStack, I18n.format("tails.gui.mountpoint"), 5, spacing * 6 + this.font.FONT_HEIGHT / 2f, GuiEditor.TEXT_COLOUR);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    protected void onChangeMountPointButtonPressed(Button button)
    {
        final OutfitPart outfitPart = parent.getCurrentOutfitPart();
        if (outfitPart == null) return;

        // Move to next enum for MointPoint or back to 0 if at the end
        final int mountPointOrdinalNext = outfitPart.mountPoint.ordinal() + 1;
        final int mountPointOrdinal = mountPointOrdinalNext >= MountPoint.values().length ? 0 : mountPointOrdinalNext;

        outfitPart.mountPoint = MountPoint.values()[mountPointOrdinal];

        mountPointButton.setMessage(new TranslationTextComponent("tails.mountpoint." + outfitPart.mountPoint.name()));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        xPosInput.mouseClicked(mouseX, mouseY, mouseButton);
        yPosInput.mouseClicked(mouseX, mouseY, mouseButton);
        zPosInput.mouseClicked(mouseX, mouseY, mouseButton);

        xRotInput.mouseClicked(mouseX, mouseY, mouseButton);
        yRotInput.mouseClicked(mouseX, mouseY, mouseButton);
        zRotInput.mouseClicked(mouseX, mouseY, mouseButton);

        xScaleInput.mouseClicked(mouseX, mouseY, mouseButton);
        yScaleInput.mouseClicked(mouseX, mouseY, mouseButton);
        zScaleInput.mouseClicked(mouseX, mouseY, mouseButton);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char key, int keyCode)
    {
        xPosInput.keyTyped(key, keyCode);
        yPosInput.keyTyped(key, keyCode);
        zPosInput.keyTyped(key, keyCode);

        xRotInput.keyTyped(key, keyCode);
        yRotInput.keyTyped(key, keyCode);
        zRotInput.keyTyped(key, keyCode);

        xScaleInput.keyTyped(key, keyCode);
        yScaleInput.keyTyped(key, keyCode);
        zScaleInput.keyTyped(key, keyCode);

        super.keyTyped(key, keyCode);
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
