package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class GuiSlider extends Widget implements IControl<Float>
{
    private final float minValue;
    private final float maxValue;
    private float currentValue;
    private double sliderValue;
    private IControlCallback<GuiSlider, Float> parent;

    public GuiSlider(int x, int y, int width, int height, float minValue, float maxValue, float defaultValue)
    {
        super(x, y, width, height, new StringTextComponent(String.valueOf(defaultValue)));
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = defaultValue;
        this.sliderValue = MathHelper.clamp((this.currentValue - this.minValue) / (this.maxValue - this.minValue), 0.0F, 1.0F);
    }

    public GuiSlider(int x, int y, int width, float minValue, float maxValue, float defaultValue)
    {
        this(x, y, width, 20, minValue, maxValue, defaultValue);
    }

    public GuiSlider(IControlCallback<GuiSlider, Float> parent, int x, int y, int width, float minValue, float maxValue, float defaultValue)
    {
        this(x, y, width, minValue, maxValue, defaultValue);
        this.parent = parent;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            Minecraft.getInstance().textureManager.bindTexture(WIDGETS_LOCATION);
            GuiUtils.drawTexturedModalRect(matrixStack, (int) (this.x + (this.sliderValue * (this.width - 8))), this.y, 0, 66, 4, this.height, 0);
            GuiUtils.drawTexturedModalRect(matrixStack, (int) (this.x + (this.sliderValue * (this.width - 8)) + 4), this.y, 196, 66, 4, this.height, 0);
        }
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY)
    {
        updateValues(mouseX, mouseY);
    }

    @Override
    public void onClick(double mouseX, double mouseY)
    {
        updateValues(mouseX, mouseY);
    }

    @Override
    public void onRelease(double mouseX, double mouseY)
    {
        updateValues(mouseX, mouseY);
    }

    @Override
    public void setValue(Float newValue)
    {
        this.currentValue = newValue;
        this.sliderValue = MathHelper.clamp((this.currentValue - this.minValue) / (this.maxValue - this.minValue), .0f, 1.f);
        setMessage(new StringTextComponent(String.valueOf(this.currentValue)));
    }

    @Override
    public Float getValue()
    {
        return this.currentValue;
    }

    private void updateValues(double xPos, double yPos)
    {
        float prevValue = this.currentValue;

        this.sliderValue = MathHelper.clamp((xPos - (this.x + 4f)) / (this.width - 8f), 0f, 1f);
        this.currentValue = (int) (this.sliderValue * (this.maxValue - this.minValue) + this.minValue);

        if (this.parent != null && !this.parent.onValueChange(this, prevValue, this.currentValue))
        {
            setValue(prevValue);
        }
        else
        {
            setMessage(new StringTextComponent(String.valueOf(this.currentValue)));
        }
    }
}
