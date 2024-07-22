package uk.kihira.tails.client.gui.controls;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import uk.kihira.tails.client.Colour;
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
            //graphics.blitSprite(WIDGETS_LOCATION, this.getX(), this.getY(), 0, 46, this.width, this.height, 200, 20, 2, 3, 2, 2, 0);
            graphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), Colour.BLACK);

            // Needed so the saturation overlapping textures works
            RenderSystem.enableBlend();

            if (this.type == HSBSliderType.SATURATION)
            {
                var hueColour = Color.getHSBColor(this.hueValue, 1F, 1F);
                var red = (float) hueColour.getRed() / 255f;
                var green = (float) hueColour.getGreen() / 255f;
                var blue = (float) hueColour.getBlue() / 255f;
                graphics.setColor(red, green, blue, 1.0F);
                graphics.blit(SLIDER_TEXTURE, this.getX() + 1, this.getY() + 1, this.width - 2, this.height - 2,0, 176, 256, 20, 256, 256);
                graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
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
                var red = (float) hueColour.getRed() / 255f;
                var green = (float) hueColour.getGreen() / 255f;
                var blue = (float) hueColour.getBlue() / 255f;
                graphics.setColor(red, green, blue, 1F);
                graphics.blit(SLIDER_TEXTURE, this.getX() + 1, this.getY() + 1, this.width - 2, this.height - 2,0, srcY, 231, 20, 256, 256);
                graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            } 
            else
            {
                graphics.blit(SLIDER_TEXTURE, this.getX() + 1, this.getY() + 1, this.width - 2, this.height - 2,0, srcY, 256, 20, 256, 256);
            }

            graphics.enableScissor(this.getX(), this.getY(), this.getRight(), this.getBottom());
            graphics.blit(SLIDER_TEXTURE, this.getX() + (int)(this.value * (double)(this.width - 3) - 2), this.getY(), 0, 0, 7, 4);
            graphics.blit(SLIDER_TEXTURE, this.getX() + (int)(this.value * (double)(this.width - 3) - 2), this.getY() + this.height - 4, 7, 0, 7, 4);
            graphics.disableScissor();
            
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
    
    private void drawTexturedModalRectScaled(GuiGraphics graphics, int x, int y, int u, int v, int srcWidth, int srcHeight, int tarWidth, int tarHeight)
    {
        Minecraft.getInstance().textureManager.bindForSetup(SLIDER_TEXTURE);

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
