package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiSlider extends GuiButtonExt implements IControl<Float> {
    private final float minValue;
    private final float maxValue;
    private float currentValue;
    private float sliderValue;
    private boolean dragging = false;
    private IControlCallback<GuiSlider, Float> parent;

    public GuiSlider(int id, int x, int y, int width, int height, float minValue, float maxValue, float defaultValue) {
        super(id, x, y, width, height, String.valueOf(defaultValue));
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = defaultValue;
        this.sliderValue = MathHelper.clamp((this.currentValue - this.minValue) / (this.maxValue - this.minValue), 0.0F, 1.0F);
    }

    public GuiSlider(int id, int x, int y, int width, float minValue, float maxValue, float defaultValue) {
        this(id, x, y, width, 20, minValue, maxValue, defaultValue);
    }

    public GuiSlider(IControlCallback<GuiSlider, Float> parent, int id, int x, int y, int width, float minValue, float maxValue, float defaultValue) {
        this(id, x, y, width, minValue, maxValue, defaultValue);
        this.parent = parent;
    }

    @Override
    protected void mouseDragged(Minecraft minecraft, int xPos, int yPos) {
        if (visible) {
            if (dragging) {
                updateValues(xPos, yPos);
            }

            minecraft.renderEngine.bindTexture(BUTTON_TEXTURES);
            drawTexturedModalRect((int) (x + (sliderValue * (width - 8))), y, 0, 66, 4, height);
            drawTexturedModalRect((int) (x + (sliderValue * (width - 8)) + 4), y, 196, 66, 4, height);
        }
    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int xPos, int yPos) {
        if (super.mousePressed(minecraft, xPos, yPos)) {
            updateValues(xPos, yPos);
            dragging = true;
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public int getHoverState(boolean bool) {
        return 0;
    }

    @Override
    public void mouseReleased(int xPos, int yPos) {
        dragging = false;
    }

    @Override
    public void setValue(Float newValue) {
        currentValue = newValue;
        sliderValue = MathHelper.clamp((currentValue - minValue) / (maxValue - minValue), .0f, 1.f);
        displayString = String.valueOf(currentValue);
    }

    @Override
    public Float getValue() {
        return currentValue;
    }

    private void updateValues(int xPos, int yPos) {
        float prevValue = currentValue;

        sliderValue = MathHelper.clamp((xPos - (x + 4f)) / (width - 8f), 0f, 1f);
        currentValue = (int) (sliderValue * (maxValue - minValue) + minValue);

        if (parent != null && !parent.onValueChange(this, prevValue, currentValue)) {
            setValue(prevValue);
        }
        else displayString = String.valueOf(currentValue);
    }
}
