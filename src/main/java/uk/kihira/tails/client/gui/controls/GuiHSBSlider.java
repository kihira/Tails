package uk.kihira.tails.client.gui.controls;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import uk.kihira.tails.client.gui.ITooltip;
import uk.kihira.tails.common.Tails;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.gui.widget.Slider;

import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
public class GuiHSBSlider extends Slider implements ITooltip 
{
    private static final ResourceLocation SLIDER_TEXTURE = new ResourceLocation(Tails.MOD_ID, "texture/gui/controls/slider_hue.png");
    
    private final HSBSliderType type;
    private final IHSBSliderCallback callback;
    private float hueValue;
    private float briValue;
    private List<String> tooltips;
    
    public GuiHSBSlider(int xPos, int yPos, int width, int height, IHSBSliderCallback callback, HSBSliderType type)
    {
        super(xPos, yPos, width, height, StringTextComponent.EMPTY, StringTextComponent.EMPTY, 0, 256 * 6 - 5, 0, false, false, (button) -> {});
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
    public void renderBg(MatrixStack matrixStack, Minecraft mc, int mouseX, int mouseY)
    {
        if (this.visible) 
        {
            GuiUtils.drawContinuousTexturedBox(matrixStack, WIDGETS_LOCATION, this.x, this.y, 0, 46, this.width, this.height, 200, 20, 2, 3, 2, 2, 0);
            Minecraft.getInstance().textureManager.bindTexture(SLIDER_TEXTURE);
            
            if (this.type == HSBSliderType.SATURATION)
            {
                Color hueColour = Color.getHSBColor(this.hueValue, 1F, 1F);
                float red = (float) hueColour.getRed() / 255;
                float green = (float) hueColour.getGreen() / 255;
                float blue = (float) hueColour.getBlue() / 255;
                GlStateManager.color4f(red, green, blue, 1.0F);
                drawTexturedModalRectScaled(x + 1, y + 1, 0, 176, 256, 20, this.width - 2, this.height - 2);
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
                GlStateManager.color4f(red, green, blue, 1.0F);
                drawTexturedModalRectScaled(x + 1, y + 1, 0, srcY, 231, 20, this.width - 2, this.height - 2);
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            } 
            else
            {
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                drawTexturedModalRectScaled(x + 1, y + 1, 0, srcY, 256, 20, this.width - 2, this.height - 2);
            }

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            //RenderHelper.startGlScissor(x, y, width, height);
            Minecraft.getInstance().textureManager.bindTexture(SLIDER_TEXTURE);
            GuiUtils.drawTexturedModalRect(matrixStack, this.x + (int)(this.sliderValue * (float)(this.width - 3) - 2), this.y, 0, 0, 7, 4, 0);
            GuiUtils.drawTexturedModalRect(matrixStack, this.x + (int)(this.sliderValue * (float)(this.width - 3) - 2), this.y + this.height - 4, 7, 0, 7, 4, 0);
            //RenderHelper.endGlScissor();
            
            if (this.dragging)
            {
                this.sliderValue = (x - (this.x + 4)) / (float)(this.width - 8);
                updateSlider();

                if (callback != null)
                {
                    callback.onValueChangeHSBSlider(this, this.sliderValue);
                }
            }
        }
    }
    
    public HSBSliderType getType() 
    {
        return type;
    }
    
    @Override
    public double getValue() 
    {
        return sliderValue;
    }

    /**
     * Sets the current slider value between 0-1F
     * @param value New value
     */
    @Override
    public void setValue(double value)
    {
        assert(value >= 0f && value <= 1f);

        this.sliderValue = value;
        updateSlider();
    }

    /**
     * Sets the current slider value between 0-1F and calls the callback
     * @param value New value
     */
    public void setValueWithCallback(double value)
    {
        assert(value >= 0f && value <= 1f);

        this.sliderValue = value;
        updateSlider();

        if (callback != null) {
            callback.onValueChangeHSBSlider(this, this.sliderValue);
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
        BufferBuilder renderer = Tessellator.getInstance().getBuffer();
        renderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        renderer.pos(x + 0, y + tarHeight, 0).tex(((float) (u + 0) * f), ((float) (v + srcHeight) * f1)).endVertex();
        renderer.pos(x + tarWidth, y + tarHeight, 0).tex(((float) (u + srcWidth) * f), ((float) (v + srcHeight) * f1)).endVertex();
        renderer.pos(x + tarWidth, y + 0, 0).tex(((float) (u + srcWidth) * f), ((float) (v + 0) * f1)).endVertex();
        renderer.pos(x + 0, y + 0, 0).tex(((float) (u + 0) * f), ((float) (v + 0) * f1)).endVertex();
        Tessellator.getInstance().draw();
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
