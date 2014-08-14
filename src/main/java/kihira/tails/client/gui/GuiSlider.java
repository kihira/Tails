package kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.MathHelper;

/**
 * Copied from GuiSlider in FoxLib
 */
public class GuiSlider extends GuiButton {

    public int currentValue;
    private float minValue;
    private float maxValue;
    private float sliderValue;
    private boolean dragging = false;
    private ISliderCallback parent;

    public GuiSlider(int id, int x, int y, int width, float minValue, float maxValue, int defaultValue) {
        super(id, x, y, width, 20, String.valueOf(defaultValue));
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = defaultValue;
        this.sliderValue = MathHelper.clamp_float((this.currentValue - this.minValue) / (this.maxValue - this.minValue), 0.0F, 1.0F);
    }

    public GuiSlider(ISliderCallback parent, int id, int x, int y, int width, float minValue, float maxValue, int defaultValue) {
        this(id, x, y, width, minValue, maxValue, defaultValue);
        this.parent = parent;
    }


    @Override
    protected void mouseDragged(Minecraft minecraft, int xPos, int yPos) {
        if (this.visible) {
            if (this.dragging) {
                this.updateValues(xPos, yPos);
            }

            this.drawTexturedModalRect((int) (this.xPosition + (this.sliderValue * (this.width - 8))), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect((int) (this.xPosition + (this.sliderValue * (this.width - 8)) + 4), this.yPosition, 196, 66, 4, 20);
        }
    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int xPos, int yPos) {
        if (super.mousePressed(minecraft, xPos, yPos)) {
            this.updateValues(xPos, yPos);
            this.dragging = true;
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
        this.dragging = false;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
        this.sliderValue = MathHelper.clamp_float((this.currentValue - this.minValue) / (this.maxValue - this.minValue), 0.0F, 1.0F);
        this.displayString = String.valueOf(this.currentValue);
    }

    private void updateValues(int xPos, int yPos) {
        int prevValue = this.currentValue;

        this.sliderValue = MathHelper.clamp_float((xPos - (this.xPosition + 4F)) / (this.width - 8F), 0F, 1F);
        this.currentValue = (int) (this.sliderValue * (this.maxValue - this.minValue) + this.minValue);

        if (this.parent != null && !this.parent.onValueChange(this, prevValue, this.currentValue)) {
            this.setCurrentValue(prevValue);
        }
        else this.displayString = String.valueOf(this.currentValue);
    }
}
