package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.config.GuiButtonExt;

@OnlyIn(Dist.CLIENT)
public class GuiSlider extends GuiButtonExt implements IControl<Double> {
    private final double minValue;
    private final double maxValue;
    private double currentValue;
    private double sliderValue;
    private boolean dragging = false;
    private IControlCallback<GuiSlider, Double> parent;

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

    public GuiSlider(IControlCallback<GuiSlider, Double> parent, int id, int x, int y, int width, float minValue, float maxValue, float defaultValue) {
        this(id, x, y, width, minValue, maxValue, defaultValue);
        this.parent = parent;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        if (visible) {
            if (dragging) {
                updateValues(mouseX, mouseY);
            }

            Minecraft.getInstance().getTextureManager().bindTexture(BUTTON_TEXTURES);
            drawTexturedModalRect((int) (x + (sliderValue * (width - 8))), y, 0, 66, 4, height);
            drawTexturedModalRect((int) (x + (sliderValue * (width - 8)) + 4), y, 196, 66, 4, height);
        }

        return super.mouseDragged(mouseX, mouseY, mouseButton, p_mouseDragged_6_, p_mouseDragged_8_);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (super.mouseClicked(mouseX, mouseY, mouseButton)) {
            updateValues(mouseX, mouseY);
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
    public void setValue(Double newValue) {
        currentValue = newValue;
        sliderValue = MathHelper.clamp((currentValue - minValue) / (maxValue - minValue), .0f, 1.f);
        displayString = String.valueOf(currentValue);
    }

    @Override
    public Double getValue() {
        return currentValue;
    }

    private void updateValues(double mouseX, double mouseY) {
        double prevValue = currentValue;

        sliderValue = MathHelper.clamp((mouseX - (x + 4f)) / (width - 8f), 0f, 1f);
        currentValue = (int) (sliderValue * (maxValue - minValue) + minValue);

        if (parent != null && !parent.onValueChange(this, prevValue, currentValue)) {
            setValue(prevValue);
        }
        else displayString = String.valueOf(currentValue);
    }
}
