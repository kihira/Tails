package uk.kihira.tails.client.gui;

import com.google.common.base.Strings;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.client.gui.widget.ExtendedSlider;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import uk.kihira.tails.client.Colour;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.client.Part;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.client.gui.controls.GuiHSBSlider;
import uk.kihira.tails.common.Tails;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Optional;

public class TintPanel extends Panel<GuiEditor> implements GuiHSBSlider.IHSBSliderCallback
{
    private static final int TINT_1_BUTTON_ID = 0;
    private static final int TINT_2_BUTTON_ID = 1;
    private static final int TINT_3_BUTTON_ID = 2;

    private static final int SLIDER_WIDTH = 120;
    private static final int SLIDER_HEIGHT = 10;
    private static final int COLOUR_PREVIEW_SIZE = 10;
    private static final int COLOUR_PREVIEW_OFFSET = 8;

    private static final int EDIT_PANEL_TOP = 43;

    private int currTintEdit = 0;
    private int currTintColour = GuiEditor.TEXT_COLOUR;
    private EditBox hexText;
    private GuiHSBSlider[] hsbSliders;
    private GuiHSBSlider[] rgbSliders;
    private IconButton tintReset;
    private IconButton colourPicker;
    private ByteBuffer pixelBuffer;
    private boolean selectingColour = false;

    TintPanel(GuiEditor parent, int left, int top, int width, int height)
    {
        super(parent, left, top, width, height);
        this.alwaysReceiveMouse = true;

        final int tintButtonY = 20;

        // Edit tint buttons
        this.addChild(new TintButton(this.getX() + 10, this.getY() + tintButtonY, TINT_1_BUTTON_ID, Colour.BLUE, this::onTintButtonPushed));
        this.addChild(new TintButton(this.getX() + 40, this.getY() + tintButtonY, TINT_2_BUTTON_ID, Colour.RED, this::onTintButtonPushed));
        this.addChild(new TintButton(this.getX() + 70, this.getY() + tintButtonY, TINT_3_BUTTON_ID, Colour.GREEN, this::onTintButtonPushed));

        //Tint edit pane
        this.addChild(hexText = new EditBox(this.font(), this.getX() + 30, this.getY() + EDIT_PANEL_TOP + 20, 73, 10, Component.empty()));
        hexText.setMaxLength(6);
        hexText.setValue(Integer.toHexString(currTintColour));
        hexText.setResponder(value ->
        {
            try
            {
                //Gets the current colour from the hex text
                var text = this.hexText.getValue();
                if (text.length() == 6)
                {
                    this.currTintColour = Integer.parseInt(text, 16);
                }
            }
            catch (NumberFormatException ignored) { }

            updateTints(false);
        });

        //RGB sliders
        rgbSliders = new GuiHSBSlider[3];
        rgbSliders[0] = new GuiHSBSlider(this.getX() + 5, this.getY() + EDIT_PANEL_TOP + 70, SLIDER_WIDTH, SLIDER_HEIGHT, 255, 1, this, GuiHSBSlider.HSBSliderType.SATURATION);
        rgbSliders[1] = new GuiHSBSlider(this.getX() + 5, this.getY() + EDIT_PANEL_TOP + 80, SLIDER_WIDTH, SLIDER_HEIGHT, 255, 1, this, GuiHSBSlider.HSBSliderType.SATURATION);
        rgbSliders[2] = new GuiHSBSlider(this.getX() + 5, this.getY() + EDIT_PANEL_TOP + 90, SLIDER_WIDTH, SLIDER_HEIGHT, 255, 1, this, GuiHSBSlider.HSBSliderType.SATURATION);
        rgbSliders[0].setHue(0f);
        rgbSliders[1].setHue(1f / 3f);
        rgbSliders[2].setHue(2f / 3f);

        addChild(rgbSliders[0]);
        addChild(rgbSliders[1]);
        addChild(rgbSliders[2]);

        //HBS sliders
        hsbSliders = new GuiHSBSlider[3];
        hsbSliders[0] = new GuiHSBSlider(this.getX() + 5, this.getY() + EDIT_PANEL_TOP + 35, SLIDER_WIDTH, SLIDER_HEIGHT, 1f, 0f, this, GuiHSBSlider.HSBSliderType.HUE);
        hsbSliders[1] = new GuiHSBSlider(this.getX() + 5, this.getY() + EDIT_PANEL_TOP + 45, SLIDER_WIDTH, SLIDER_HEIGHT, 1f, 0f, this, GuiHSBSlider.HSBSliderType.SATURATION);
        hsbSliders[2] = new GuiHSBSlider(this.getX() + 5, this.getY() + EDIT_PANEL_TOP + 55, SLIDER_WIDTH, SLIDER_HEIGHT, 1f, 0f, this, GuiHSBSlider.HSBSliderType.BRIGHTNESS);

        addChild(hsbSliders[0]);
        addChild(hsbSliders[1]);
        addChild(hsbSliders[2]);

        //Reset/Save
        addChild(tintReset = new IconButton(this.getRight() - 20, this.getY() + EDIT_PANEL_TOP + 5, IconButton.Icons.UNDO, this::onResetButtonPressed, Component.translatable("gui.button.reset")));
        tintReset.active = false;

        //Colour Picker
        addChild(colourPicker = new IconButton(this.getRight() - 36, this.getY() + EDIT_PANEL_TOP + 5, IconButton.Icons.EYEDROPPER, this::onColourPickerButtonPressed, Component.translatable("gui.button.picker.0"), Component.translatable("gui.button.picker.1")));
        colourPicker.visible = false;

        updateTints(true);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        RenderSystem.enableBlend();
        graphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), Colour.DARK_GREY);
        graphics.fill(this.getX(), this.getY(), this.getRight(), this.getY() + EDIT_PANEL_TOP, Colour.LIGHT_GRAY); // tint button pos + tint button size + tint button border size
        graphics.drawString(font(), Component.translatable("gui.tint"), this.getX() + 5, this.getY() + 3, GuiEditor.TEXT_COLOUR);

        //Editing tint pane
        if (this.currTintEdit >= 0)
        {
            graphics.hLine(this.getX(), this.getRight(), EDIT_PANEL_TOP, Colour.BLACK);
            graphics.drawString(font(), Component.translatable("gui.tint.edit", this.currTintEdit + 1), this.getX() + 5, this.getY() + EDIT_PANEL_TOP + 5, GuiEditor.TEXT_COLOUR);

            graphics.drawString(font(), Component.translatable("gui.hex").append(":"),  this.getX() + 5,  this.getY() + EDIT_PANEL_TOP + 21, GuiEditor.TEXT_COLOUR);
            this.hexText.setVisible(true);
        }
        else
        {
            this.hexText.setVisible(false);
        }

        // Draw preview of colour hovered over
        if (this.selectingColour)
        {
            final int x = mouseX + COLOUR_PREVIEW_OFFSET;
            final int y = mouseY + COLOUR_PREVIEW_OFFSET;
            final int colour = getColourAtPoint(Minecraft.getInstance().mouseHandler.xpos(), Minecraft.getInstance().mouseHandler.ypos());
            graphics.fillGradient(0, x, y, x + COLOUR_PREVIEW_SIZE, y + COLOUR_PREVIEW_SIZE, colour, colour);
        }

        super.renderWidget(graphics, mouseX, mouseY, partialTicks);
    }

    private void onTintButtonPushed(GuiEventListener button)
    {
        currTintEdit = ((TintButton) button).getTintId();
        currTintColour = tintToArgb(parent.getCurrentOutfitPart().tint[currTintEdit]);
        updateTints(true);
        tintReset.active = false;
        colourPicker.active = true;
    }

    private void onResetButtonPressed(GuiEventListener button)
    {
        final OutfitPart currentPart = parent.getCurrentOutfitPart();
        if (currentPart == null) return;

        final Optional<Part> basePart = PartRegistry.getPart(currentPart.basePart);
        if (basePart.isEmpty())
        {
            Tails.LOGGER.error("Current outfit part does not have a base part available, this is an invalid state!");
            return;
        }

        currTintColour = tintToArgb(basePart.get().tint[currTintEdit]);
        updateTints(true);
        tintReset.active = false;
    }

    private void onColourPickerButtonPressed(GuiEventListener button)
    {
        setSelectingColour(true);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers)
    {
        if (codePoint == GLFW.GLFW_KEY_ESCAPE && this.selectingColour)
        {
            setSelectingColour(false);
            return true;
        }
        else
        {
            return super.charTyped(codePoint, modifiers);
        }
    }

/*    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        if (this.selectingColour && mouseButton == 0)
        {
            this.currTintColour = getColourAtPoint(Minecraft.getInstance().mouseHandler.xpos(), Minecraft.getInstance().mouseHandler.ypos()); //Ignore alpha
            setSelectingColour(false);
            updateTints(true);
            return true;
        }
        else
        {
            this.hexText.mouseClicked(mouseX, mouseY, mouseButton);
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }*/

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    @Override
    public void onValueChangeHSBSlider(GuiHSBSlider source, double sliderValue)
    {
        if (source == this.rgbSliders[0] || source == this.rgbSliders[1] || source == this.rgbSliders[2])
        {
            this.currTintColour = new Color(
                    this.rgbSliders[0].getValueInt(),
                    this.rgbSliders[1].getValueInt(),
                    this.rgbSliders[2].getValueInt()).getRGB();
        }
        else
        {
            float[] hsbvals = {(float) this.hsbSliders[0].getValue(), (float) this.hsbSliders[1].getValue(), (float) this.hsbSliders[2].getValue()};
            hsbvals[source.getType().ordinal()] = (float) sliderValue;
            this.currTintColour = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]).getRGB();
        }
        updateTints(true);
    }

    /**
     * Converts a vec3 of floats to the ARGB int format used by Minecraft
     * @param tint The tint
     */
    protected int tintToArgb(float[] tint)
    {
        int col = Colour.BLACK;
        col |= (int)(tint[0] * 255f) << 16;
        col |= (int)(tint[1] * 255f) << 8;
        col |= (int)(tint[2] * 255f);
        return col;
    }

    /**
     * Converts a ARGB int to a vec3 of floats
     * @param argb The int value
     * @return A vec3 of RGB
     */
    protected float[] argbToTint(int argb)
    {
        float[] tint = new float[3];
        tint[0] = ((argb >> 16) & 0xFF) / 255f;
        tint[1] = ((argb >> 8) & 0xFF) / 255f;
        tint[2] = (argb & 0xFF) / 255f;
        return tint;
    }

    private int getColourAtPoint(double x, double y)
    {
        byte[] pixelData;
        int pixels = 1;

        if (this.pixelBuffer == null)
        {
            this.pixelBuffer = BufferUtils.createByteBuffer(pixels);
        }
        pixelData = new byte[pixels];

        GlStateManager._pixelStore(GL11.GL_PACK_ALIGNMENT, 1);
        GlStateManager._pixelStore(GL11.GL_UNPACK_ALIGNMENT, 1);
        this.pixelBuffer.clear();

        GlStateManager._readPixels((int) x, (int) y, 1, 1, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, this.pixelBuffer);

        this.pixelBuffer.get(pixelData);

        return pixelData[0];
    }

    private void setSelectingColour(boolean selectingColour)
    {
        this.selectingColour = selectingColour;

        if (selectingColour)
        {
            try
            {
                final int cursorSize = 16;
                BufferedImage bufferedImage = ImageIO.read(minecraft().getResourceManager().getResource(IconButton.ICONS_TEXTURES).get().open());
                int[] pixelData;
                int pixels = cursorSize * cursorSize;
                pixelData = new int[pixels];
                IntBuffer buffer = IntBuffer.wrap(bufferedImage.getRGB(
                        IconButton.Icons.EYEDROPPER.u,
                        IconButton.Icons.EYEDROPPER.v + cursorSize,
                        cursorSize,
                        cursorSize,
                        pixelData,
                        0,
                        cursorSize));

                // TODO Fix custom cursor
//                int cursor = GLFW.glfwCreateCursor(buffer, 0, 15);
//                GLFW.glfwSetCursor(Minecraft.getInstance().getMainWindow().getHandle(), cursor);
            }
            catch (IOException e)
            {
                Tails.LOGGER.error(e);
            }
        }
        else {
//            try {
//                Mouse.setNativeCursor(null);
//            } catch (LWJGLException e) {
//                e.printStackTrace();
//            }
        }
    }

    /**
     * Updates the controls values for this panel as well as updates the tints for the selected OutfitPart
     *
     * @param updateHex Whether the hex text should be updated
     */
    void updateTints(boolean updateHex)
    {
        this.hexText.setTextColor(this.currTintColour);
        if (updateHex)
        {
            this.hexText.setMessage(Component.literal(Integer.toHexString(this.currTintColour)));
        }

        //RGB Sliders
        Color c = new Color(currTintColour);
        rgbSliders[0].setValue(c.getRed());
        rgbSliders[1].setValue(c.getGreen());
        rgbSliders[2].setValue(c.getBlue());

        //HSB Sliders
        float[] hsbvals = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        hsbSliders[0].setValue(hsbvals[0]);
        hsbSliders[1].setValue(hsbvals[1]);
        hsbSliders[2].setValue(hsbvals[2]);
        //The saturation slider needs to know the value of the other 2 sliders
        hsbSliders[1].setHue((float) hsbSliders[0].getValue());
        hsbSliders[1].setBrightness((float) hsbSliders[2].getValue());

        if (currTintEdit >= 0)
        {
            rgbSliders[0].visible = rgbSliders[1].visible = rgbSliders[2].visible = true;
            hsbSliders[0].visible = hsbSliders[1].visible = hsbSliders[2].visible = true;
            tintReset.visible = true;
            colourPicker.visible = true;
        }
        else
        {
            rgbSliders[0].visible = rgbSliders[1].visible = rgbSliders[2].visible = false;
            hsbSliders[0].visible = hsbSliders[1].visible = hsbSliders[2].visible = false;
            tintReset.visible = false;
            colourPicker.visible = false;
        }

        tintReset.active = true;

        if (parent.getCurrentOutfitPart() != null)
        {
            parent.getCurrentOutfitPart().tint[currTintEdit] = argbToTint(currTintColour);
        }
    }

    private class TintButton extends ExtendedButton
    {
        static final int BTN_SIZE = 20;
        private static final int HIGHLIGHT_COLOR = 0xFFCCC23D;
        private static final int OUTLINE_BORDER = 5;
        private static final int HIGHLIGHT_BORDER = 1;

        private final int tintId;
        private final int defaultColour;

        TintButton(int x, int y, int tintId, int defaultColour, OnPress pressedAction)
        {
            super(x, y, BTN_SIZE, BTN_SIZE, Component.translatable("gui.tint"), pressedAction);
            this.tintId = tintId;
            this.defaultColour = defaultColour;
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
        {
            if (currTintEdit == tintId)
            {
                graphics.fill(
                        this.getX() - OUTLINE_BORDER,
                        this.getY() - OUTLINE_BORDER,
                        this.getX() + width + OUTLINE_BORDER,
                        this.getY() + height + OUTLINE_BORDER,
                        Colour.LIGHT_GRAY);
                graphics.hLine(this.getX() - OUTLINE_BORDER, this.getX() + width + OUTLINE_BORDER - 1, this.getY() - OUTLINE_BORDER, Colour.BLACK);
                graphics.vLine(this.getX() - OUTLINE_BORDER, this.getY() - OUTLINE_BORDER, this.getY() + height + OUTLINE_BORDER, Colour.BLACK);
                graphics.vLine(this.getX() + width + OUTLINE_BORDER - 1, this.getY() - OUTLINE_BORDER, this.getY() + height + OUTLINE_BORDER, Colour.BLACK);
            }
            else if (this.isHovered)
            {
                graphics.fill(
                        this.getX() - HIGHLIGHT_BORDER,
                        this.getY() - HIGHLIGHT_BORDER,
                        this.getX() + width + HIGHLIGHT_BORDER,
                        this.getY() + height + HIGHLIGHT_BORDER,
                        HIGHLIGHT_COLOR);
            }

            final OutfitPart currentPart = parent.getCurrentOutfitPart();
            final int colour = currentPart != null ? tintToArgb(currentPart.tint[tintId]) : defaultColour;
            graphics.fill(this.getX(), this.getY(), this.getX() + width, this.getY() + height, colour);
        }

        public int getTintId()
        {
            return tintId;
        }
    }
}
