/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.math.MathHelper;

public class GuiSlider extends GuiButton implements IControl<Float> {

    public float currentValue;
    private final float minValue;
    private final float maxValue;
    private float sliderValue;
    private boolean dragging = false;
    private IControlCallback<GuiSlider, Float> parent;

    public GuiSlider(int id, int x, int y, int width, float minValue, float maxValue, float defaultValue) {
        super(id, x, y, width, 20, String.valueOf(defaultValue));
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = defaultValue;
        this.sliderValue = MathHelper.clamp((this.currentValue - this.minValue) / (this.maxValue - this.minValue), 0.0F, 1.0F);
    }

    public GuiSlider(IControlCallback<GuiSlider, Float> parent, int id, int x, int y, int width, float minValue, float maxValue, float defaultValue) {
        this(id, x, y, width, minValue, maxValue, defaultValue);
        this.parent = parent;
    }

    @Override
    protected void mouseDragged(Minecraft minecraft, int xPos, int yPos) {
        if (this.visible) {
            if (this.dragging) {
                this.updateValues(xPos, yPos);
            }

            minecraft.renderEngine.bindTexture(BUTTON_TEXTURES);
            this.drawTexturedModalRect((int) (this.x + (this.sliderValue * (this.width - 8))), this.y, 0, 66, 4, 20);
            this.drawTexturedModalRect((int) (this.x + (this.sliderValue * (this.width - 8)) + 4), this.y, 196, 66, 4, 20);
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

    @Override
    public void setValue(Float currentValue) {
        this.currentValue = currentValue;
        this.sliderValue = MathHelper.clamp((this.currentValue - this.minValue) / (this.maxValue - this.minValue), 0.0F, 1.0F);
        this.displayString = String.valueOf(this.currentValue);
    }

    @Override
    public Float getValue() {
        return currentValue;
    }

    private void updateValues(int xPos, int yPos) {
        float prevValue = this.currentValue;

        this.sliderValue = MathHelper.clamp((xPos - (this.x + 4F)) / (this.width - 8F), 0F, 1F);
        this.currentValue = (int) (this.sliderValue * (this.maxValue - this.minValue) + this.minValue);

        if (this.parent != null && !this.parent.onValueChange(this, prevValue, this.currentValue)) {
            this.setValue(prevValue);
        }
        else this.displayString = String.valueOf(this.currentValue);
    }
}
