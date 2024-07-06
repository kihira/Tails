package uk.kihira.tails.client.gui.controls;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import uk.kihira.tails.client.gui.ITooltip;
import uk.kihira.tails.common.Tails;
import net.minecraft.client.Minecraft;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.blaze3d.platform.GlStateManager;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
public class GuiHSBSlider extends ExtendedSlider implements ITooltip
{
    private static final ResourceLocation SLIDER_TEXTURE = new ResourceLocation(Tails.MOD_ID, "texture/gui/controls/slider_hue.png");
    
    private final HSBSliderType type;
    private final IHSBSliderCallback callback;
    private float hueValue;
    private float briValue;
    private List<String> tooltips;
    
    public GuiHSBSlider(int xPos, int yPos, int width, int height, IHSBSliderCallback callback, HSBSliderType type)
    {
        super(xPos, yPos, width, height, Component.empty(), Component.empty(), 0, 256 * 6 - 5, 0, 1, 0, false);
        this.type = type;
        this.hueValue = 0;
        this.briValue = 0;
        this.callback = callback;
    }

    public GuiHSBSlider(int xPos, int yPos, int width, int height, IHSBSliderCallback callback, HSBSliderType type, String ... tooltips)
    {
        this(xPos, yPos, width, height, callback, type);
        this.tooltips = Arrays.asList(tooltips);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (this.visible) 
        {
            //graphics.blitSprite(WIDGETS_LOCATION, this.getX(), this.getY(), 0, 46, this.width, this.height, 200, 20, 2, 3, 2, 2, 0);
            Minecraft.getInstance().textureManager.bindForSetup(SLIDER_TEXTURE);
            
            if (this.type == HSBSliderType.SATURATION)
            {
                Color hueColour = Color.getHSBColor(this.hueValue, 1F, 1F);
                float red = (float) hueColour.getRed() / 255;
                float green = (float) hueColour.getGreen() / 255;
                float blue = (float) hueColour.getBlue() / 255;
                graphics.setColor(red, green, blue, 1.0F);
                drawTexturedModalRectScaled(this.getX() + 1, this.getY() + 1, 0, 176, 256, 20, this.width - 2, this.height - 2);
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
            else if (this.type == HSBSliderType.SATURATION)
            {
                Color hueColour = Color.getHSBColor(0F, 0F, briValue);
                float red = (float) hueColour.getRed() / 255;
                float green = (float) hueColour.getGreen() / 255;
                float blue = (float) hueColour.getBlue() / 255;
                graphics.setColor(red, green, blue, 1.0F);
                drawTexturedModalRectScaled(this.getX() + 1, this.getY() + 1, 0, srcY, 231, 20, this.width - 2, this.height - 2);
                graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            } 
            else
            {
                graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                drawTexturedModalRectScaled(this.getX() + 1, this.getY() + 1, 0, srcY, 256, 20, this.width - 2, this.height - 2);
            }

            graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            //RenderHelper.startGlScissor(x, y, width, height);
            //graphics.blitSprite(SLIDER_TEXTURE, this.getX() + (int)(this.value * (float)(this.width - 3) - 2), this.getY(), 0, 0, 7, 4, 0);
            //graphics.blitSprite(SLIDER_TEXTURE, this.getX() + (int)(this.value * (float)(this.width - 3) - 2), this.getY() + this.height - 4, 7, 0, 7, 4, 0);
            //RenderHelper.endGlScissor();
            
/*            if (this.dragging)
            {
                this.setValue((this.getX() - (this.getX() + 4)) / (double)(this.width - 8));

                if (callback != null)
                {
                    callback.onValueChangeHSBSlider(this, this.value);
                }
            }*/
        }
    }

    public HSBSliderType getType()
    {
        return type;
    }
    
    @Override
    public double getValue() 
    {
        return value;
    }

    /**
     * Sets the current slider value between 0-1F
     * @param value New value
     */
    @Override
    public void setValue(double value)
    {
        assert(value >= 0f && value <= 1f);

        this.value = value;
    }

    /**
     * Sets the current slider value between 0-1F and calls the callback
     * @param value New value
     */
    public void setValueWithCallback(double value)
    {
        this.setValue(value);

        if (callback != null)
        {
            callback.onValueChangeHSBSlider(this, this.value);
        }
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
    
    private void drawTexturedModalRectScaled(int x, int y, int u, int v, int srcWidth, int srcHeight, int tarWidth, int tarHeight)
    {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        BufferBuilder renderer = Tesselator.getInstance().getBuilder();
        renderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        renderer.vertex(x + 0, y + tarHeight, 0).uv(((float) (u + 0) * f), ((float) (v + srcHeight) * f1)).endVertex();
        renderer.vertex(x + tarWidth, y + tarHeight, 0).uv(((float) (u + srcWidth) * f), ((float) (v + srcHeight) * f1)).endVertex();
        renderer.vertex(x + tarWidth, y + 0, 0).uv(((float) (u + srcWidth) * f), ((float) (v + 0) * f1)).endVertex();
        renderer.vertex(x + 0, y + 0, 0).uv(((float) (u + 0) * f), ((float) (v + 0) * f1)).endVertex();
        Tesselator.getInstance().end();
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
}
