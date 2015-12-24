package kihira.tails.client.gui.controls;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.client.config.GuiUtils;
import kihira.foxlib.client.gui.ITooltip;
import kihira.tails.common.Tails;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class GuiHSBSlider extends GuiSlider implements ITooltip {

    private static final ResourceLocation sliderTexture = new ResourceLocation(Tails.MOD_ID.toLowerCase(), "texture/gui/controls/sliderHue.png");
    
    private final HSBSliderType type;
    private final IHSBSliderCallback callback;
    private float hueValue;
    private float briValue;
    private List<String> tooltips;
    
    public GuiHSBSlider(int id, int xPos, int yPos, int width, int height, IHSBSliderCallback callback, HSBSliderType type) {
        super(id, xPos, yPos, width, height, "", "", 0, 256 * 6 - 5, 0, false, false);
        this.type = type;
        this.hueValue = 0;
        this.briValue = 0;
        this.callback = callback;
    }

    public GuiHSBSlider(int id, int xPos, int yPos, int width, int height, IHSBSliderCallback callback, HSBSliderType type, String ... tooltips) {
        this(id, xPos, yPos, width, height, callback, type);
        this.tooltips = Arrays.asList(tooltips);
    }
    
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            GuiUtils.drawContinuousTexturedBox(buttonTextures, this.xPosition, this.yPosition, 0, 46, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
            mc.renderEngine.bindTexture(sliderTexture);
            
            if (type == HSBSliderType.SATURATION) {
                Color hueColour = Color.getHSBColor(hueValue, 1F, 1F);
                float red = (float) hueColour.getRed() / 255;
                float green = (float) hueColour.getGreen() / 255;
                float blue = (float) hueColour.getBlue() / 255;
                GL11.glColor4f(red, green, blue, 1.0F);
                drawTexturedModalRectScaled(xPosition + 1, yPosition + 1, 0, 176, 256, 20, this.width - 2, this.height - 2);
            }
            
            int srcY = 236;
            
            if (type == HSBSliderType.BRIGHTNESS) {
                srcY -= 20;
            }
            if (type == HSBSliderType.SATURATION) {
                srcY -= 40;
            }
            if (type == HSBSliderType.SATURATION) {
                Color hueColour = Color.getHSBColor(0F, 0F, briValue);
                float red = (float) hueColour.getRed() / 255;
                float green = (float) hueColour.getGreen() / 255;
                float blue = (float) hueColour.getBlue() / 255;
                GL11.glColor4f(red, green, blue, 1.0F);
                drawTexturedModalRectScaled(xPosition + 1, yPosition + 1, 0, srcY, 231, 20, this.width - 2, this.height - 2);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                drawTexturedModalRectScaled(xPosition + 1, yPosition + 1, 0, srcY, 256, 20, this.width - 2, this.height - 2);
            }
            
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }
    
    @Override
    protected void mouseDragged(Minecraft mc, int par2, int par3) {
        if (this.visible) {
            if (this.dragging) {
                this.sliderValue = (par2 - (this.xPosition + 4)) / (float)(this.width - 8);
                updateSlider();
                if (callback != null) {
                    callback.onValueChangeHSBSlider(this, this.sliderValue);
                }
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            //RenderHelper.startGlScissor(xPosition, yPosition, width, height);
            mc.renderEngine.bindTexture(sliderTexture);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 3) - 2), this.yPosition, 0, 0, 7, 4);
            this.drawTexturedModalRect(this.xPosition + (int)(this.sliderValue * (float)(this.width - 3) - 2), this.yPosition + this.height - 4, 7, 0, 7, 4);
            //RenderHelper.endGlScissor();
        }
    }
    
    public HSBSliderType getType() {
        return type;
    }
    
    @Override
    public double getValue() {
        return sliderValue;
    }

    /**
     * Sets the current slider value between 0-1F
     * @param value New value
     */
    @Override
    public void setValue(double value) {
        this.sliderValue = value;
        updateSlider();
    }

    /**
     * Sets the current slider value between 0-1F and calls the callback
     * @param value New value
     */
    public void setValueWithCallback(double value) {
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
    public void setHue(float value) {
        this.hueValue = value;
    }

    /**
     * Sets the current brightness value between 0-1F
     * @param value New value
     */
    public void setBrightness(float value) {
        this.briValue = value;
    }
    
    public void drawTexturedModalRectScaled (int x, int y, int u, int v, int srcWidth, int srcHeight, int tarWidth, int tarHeight) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();
        // TODO Port to 1.8.8
/*        renderer.startDrawingQuads();
        renderer.addVertexWithUV((double) (x + 0), (double) (y + tarHeight), (double) this.zLevel, (double) ((float) (u + 0) * f), (double) ((float) (v + srcHeight) * f1));
        renderer.addVertexWithUV((double) (x + tarWidth), (double) (y + tarHeight), (double) this.zLevel, (double) ((float) (u + srcWidth) * f), (double) ((float) (v + srcHeight) * f1));
        renderer.addVertexWithUV((double) (x + tarWidth), (double) (y + 0), (double) this.zLevel, (double) ((float) (u + srcWidth) * f), (double) ((float) (v + 0) * f1));
        renderer.addVertexWithUV((double) (x + 0), (double) (y + 0), (double) this.zLevel, (double) ((float) (u + 0) * f), (double) ((float) (v + 0) * f1));
        Tessellator.getInstance().draw();*/
    }

    @Override
    public List<String> getTooltip(int mouseX, int mouseY) {
        return tooltips;
    }

    public enum HSBSliderType {
        HUE, SATURATION, BRIGHTNESS
    }
    
    public interface IHSBSliderCallback {
        public void onValueChangeHSBSlider(GuiHSBSlider source, double sliderValue);
    }
}
