package uk.kihira.tails.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import uk.kihira.tails.client.MountPoint;
import uk.kihira.tails.client.OutfitPart;
import uk.kihira.tails.client.gui.controls.NumberInput;

import java.io.IOException;

import static uk.kihira.tails.client.gui.GuiEditor.HOZ_LINE_COLOUR;

@OnlyIn(Dist.CLIENT)
public class TransformPanel extends Panel<GuiEditor> implements IControlCallback<IControl<Float>, Float> {
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

    private GuiButtonExt mountPointButton;

    TransformPanel(GuiEditor parent, int x, int y, int width, int height) {
        super(parent, x, y, width, height);
    }

    @Override
    public void initGui() {
        xRotInput = new NumberInput(3, spacing, WIDTH, MIN_ROTATION, MAX_ROTATION, INC_ROTATION, this);
        yRotInput = new NumberInput(52, spacing, WIDTH, MIN_ROTATION, MAX_ROTATION, INC_ROTATION, this);
        zRotInput = new NumberInput(101, spacing, WIDTH, MIN_ROTATION, MAX_ROTATION, INC_ROTATION, this);

        xPosInput = new NumberInput(3, spacing * 3, WIDTH, MIN_POSITION, MAX_POSITION, INC_POSITION, this);
        yPosInput = new NumberInput(52, spacing * 3, WIDTH, MIN_POSITION, MAX_POSITION, INC_POSITION, this);
        zPosInput = new NumberInput(101, spacing * 3, WIDTH, MIN_POSITION, MAX_POSITION, INC_POSITION, this);

        xScaleInput = new NumberInput(3, spacing * 5, WIDTH, MIN_SCALE, MAX_SCALE, INC_SCALE, this);
        yScaleInput = new NumberInput(52, spacing * 5, WIDTH, MIN_SCALE, MAX_SCALE, INC_SCALE, this);
        zScaleInput = new NumberInput(101, spacing * 5, WIDTH, MIN_SCALE, MAX_SCALE, INC_SCALE, this);

        String mountPoint = MountPoint.values()[0].name();
        OutfitPart outfitPart = parent.getCurrentOutfitPart();
        if (outfitPart != null) mountPoint = outfitPart.mountPoint.name();
        mountPoint = I18n.format("tails.mountpoint." + mountPoint);

        buttons.add(mountPointButton = new GuiButtonExt(0, 5, spacing * 7, width - 10, 20, mountPoint) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);

                OutfitPart outfitPart = parent.getCurrentOutfitPart();
                if (outfitPart == null) return;

                if (outfitPart.mountPoint.ordinal() + 1 >= MountPoint.values().length) {
                    outfitPart.mountPoint = MountPoint.values()[0];
                } else {
                    outfitPart.mountPoint = MountPoint.values()[outfitPart.mountPoint.ordinal() + 1];
                }

                mountPointButton.displayString = I18n.format("tails.mountpoint." + outfitPart.mountPoint.name());
            }
        });
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        drawHorizontalLine(0, width, 0, HOZ_LINE_COLOUR);

        zLevel = -100;
        drawGradientRect(0, 0, width, height, GuiEditor.DARK_GREY, GuiEditor.DARK_GREY);
        zLevel = 0;

        // Rotation
        fontRenderer.drawString(I18n.format("tails.gui.rotation"), 5, fontRenderer.FONT_HEIGHT / 2, GuiEditor.TEXT_COLOUR);
        xRotInput.draw(mouseX, mouseY, partialTicks);
        yRotInput.draw(mouseX, mouseY, partialTicks);
        zRotInput.draw(mouseX, mouseY, partialTicks);

        // Position
        fontRenderer.drawString(I18n.format("tails.gui.position"), 5, spacing * 2 + fontRenderer.FONT_HEIGHT / 2, GuiEditor.TEXT_COLOUR);
        xPosInput.draw(mouseX, mouseY, partialTicks);
        yPosInput.draw(mouseX, mouseY, partialTicks);
        zPosInput.draw(mouseX, mouseY, partialTicks);

        // Scale
        fontRenderer.drawString(I18n.format("tails.gui.scale"), 5, spacing * 4 + fontRenderer.FONT_HEIGHT / 2, GuiEditor.TEXT_COLOUR);
        xScaleInput.draw(mouseX, mouseY, partialTicks);
        yScaleInput.draw(mouseX, mouseY, partialTicks);
        zScaleInput.draw(mouseX, mouseY, partialTicks);

        // Mount point
        fontRenderer.drawString(I18n.format("tails.gui.mountpoint"), 5, spacing * 6 + fontRenderer.FONT_HEIGHT / 2, GuiEditor.TEXT_COLOUR);

        super.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
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
    public void keyTyped(char key, int keyCode) {
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
    public boolean onValueChange(IControl<Float> control, Float oldValue, Float newValue) {
        OutfitPart outfitPart = parent.getCurrentOutfitPart();
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
}
