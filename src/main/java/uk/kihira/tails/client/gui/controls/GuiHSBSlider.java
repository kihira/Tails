package uk.kihira.tails.client.gui.controls;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import uk.kihira.tails.client.Colour;
import uk.kihira.tails.client.gui.ITooltip;
import uk.kihira.tails.Tails;

import javax.annotation.ParametersAreNonnullByDefault;


import java.awt.*;
import java.util.List;

@ParametersAreNonnullByDefault
public class GuiHSBSlider extends ExtendedSlider implements ITooltip
{
    private final HSBSliderType type;
    private final IHSBSliderCallback callback;
    private float hueValue;
    private float briValue;
    private List<String> tooltips;
    
    public GuiHSBSlider(int xPos, int yPos, int width, int height, double maxValue, double stepSize, IHSBSliderCallback callback, HSBSliderType type)
    {
        super(xPos, yPos, width, height, Component.empty(), Component.empty(), 0, maxValue, 0, stepSize, 4, false);
        this.type = type;
        this.hueValue = 0;
        this.briValue = 0;
        this.callback = callback;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.visible) 
        {
            // Background/border
            graphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), Colour.BLACK);

            // Offset the texture in 1 so we can have a border
            var x = this.getX() + 1;
            var y = this.getY() + 1;
            var width = this.width - 2;
            var height = this.height - 2;

            if (this.type == HSBSliderType.SATURATION)
            {
                var hueColour = Color.getHSBColor(this.hueValue, 1f, 1f);
                var argb = ARGB.color(hueColour.getRed(), hueColour.getGreen(), hueColour.getBlue());
                graphics.blit(RenderPipelines.GUI_TEXTURED, SLIDER_TEXTURE, x, y, 0, 176, width, height, 256, 20, TEXTURE_WIDTH, TEXTURE_HEIGHT, argb);
            }
            
            int srcY = 236;
            
            if (this.type == HSBSliderType.BRIGHTNESS)
            {
                srcY -= 20;
            }
            else if (this.type == HSBSliderType.SATURATION)
            {
                srcY -= 40;
            }

            if (this.type == HSBSliderType.SATURATION)
            {
                var hueColour = Color.getHSBColor(0F, 0F, briValue);
                var argb = ARGB.color(hueColour.getRed(), hueColour.getGreen(), hueColour.getBlue());
                graphics.blit(RenderPipelines.GUI_TEXTURED, SLIDER_TEXTURE, x, y, 0, srcY, width, height, 256, 20, TEXTURE_WIDTH, TEXTURE_HEIGHT, argb);
            }
            else
            {
                graphics.blit(RenderPipelines.GUI_TEXTURED, SLIDER_TEXTURE, x, y,0, srcY, width, height, 256, 20, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            }

            // Draw slider thumb/arrows. Scissor to prevent drawing outside of slider bounds
            graphics.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
            graphics.blit(RenderPipelines.GUI_TEXTURED, SLIDER_TEXTURE, this.getX() + (int)(this.value * (double)(this.width - 3) - 2), this.getY(), 0, 0, 7, 4, 7, 4, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            graphics.blit(RenderPipelines.GUI_TEXTURED, SLIDER_TEXTURE, this.getX() + (int)(this.value * (double)(this.width - 3) - 2), this.getY() + this.height - 4, 7, 0, 7, 4, 7, 4, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            graphics.disableScissor();
        }
    }

    @Override
    protected void applyValue()
    {
        if (this.callback != null)
        {
            this.callback.onValueChangeHSBSlider(this, this.value);
        }
    }

    public HSBSliderType getType()
    {
        return this.type;
    }

    /**
     * Sets the current hue value between 0-1F
     * @param value New value
     */
    public void setHue(float value)
    {
        assert(value >= 0f && value <= 1f);

        this.hueValue = value;
    }

    /**
     * Sets the current brightness value between 0-1F
     * @param value New value
     */
    public void setBrightness(float value)
    {
        assert(value >= 0f && value <= 1f);

        this.briValue = value;
    }

    @Override
    public List<String> getTooltip(int mouseX, int mouseY, float mouseIdleTime)
    {
        return tooltips;
    }

    public enum HSBSliderType
    {
        HUE,
        SATURATION,
        BRIGHTNESS,
    }

    public interface IHSBSliderCallback
    {
        void onValueChangeHSBSlider(GuiHSBSlider source, double sliderValue);
    }

    private static final Identifier SLIDER_TEXTURE = Identifier.fromNamespaceAndPath(Tails.MOD_ID, "texture/gui/controls/slider_hue.png");
    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
}
