package uk.kihira.tails.client.gui;

import com.google.common.base.Strings;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHelper;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
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
import java.util.Optional;

public class TintPanel extends Panel<GuiEditor> implements GuiHSBSlider.IHSBSliderCallback, IControlCallback<GuiSlider, Float>
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
    private TextFieldWidget hexText;
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
    }

    @Override
    public void init()
    {
        final int tintButtonY = 20;

        // Edit tint buttons
        this.addButton(new TintButton(10, tintButtonY, TINT_1_BUTTON_ID, Colour.BLUE, this::onTintButtonPushed));
        this.addButton(new TintButton(40, tintButtonY, TINT_2_BUTTON_ID, Colour.RED, this::onTintButtonPushed));
        this.addButton(new TintButton(70, tintButtonY, TINT_3_BUTTON_ID, Colour.GREEN, this::onTintButtonPushed));

        //Tint edit pane
        hexText = new TextFieldWidget(this.font, 30, EDIT_PANEL_TOP + 20, 73, 10, StringTextComponent.EMPTY);
        hexText.setMaxStringLength(6);

        //RGB sliders
        rgbSliders = new GuiHSBSlider[3];
        rgbSliders[0] = new GuiHSBSlider(5, EDIT_PANEL_TOP + 70, SLIDER_WIDTH, SLIDER_HEIGHT, this, GuiHSBSlider.HSBSliderType.SATURATION, I18n.format("gui.slider.red.tooltip"));
        rgbSliders[1] = new GuiHSBSlider(5, EDIT_PANEL_TOP + 80, SLIDER_WIDTH, SLIDER_HEIGHT, this, GuiHSBSlider.HSBSliderType.SATURATION, I18n.format("gui.slider.green.tooltip"));
        rgbSliders[2] = new GuiHSBSlider(5, EDIT_PANEL_TOP + 90, SLIDER_WIDTH, SLIDER_HEIGHT, this, GuiHSBSlider.HSBSliderType.SATURATION, I18n.format("gui.slider.blue.tooltip"));
        rgbSliders[0].setHue(0f);
        rgbSliders[1].setHue(1f / 3f);
        rgbSliders[2].setHue(2f / 3f);

        addButton(rgbSliders[0]);
        addButton(rgbSliders[1]);
        addButton(rgbSliders[2]);

        //HBS sliders
        hsbSliders = new GuiHSBSlider[3];
        hsbSliders[0] = new GuiHSBSlider(5, EDIT_PANEL_TOP + 35, SLIDER_WIDTH, SLIDER_HEIGHT, this, GuiHSBSlider.HSBSliderType.HUE, I18n.format("gui.slider.hue.tooltip"));
        hsbSliders[1] = new GuiHSBSlider(5, EDIT_PANEL_TOP + 45, SLIDER_WIDTH, SLIDER_HEIGHT, this, GuiHSBSlider.HSBSliderType.SATURATION, I18n.format("gui.slider.saturation.tooltip"));
        hsbSliders[2] = new GuiHSBSlider(5, EDIT_PANEL_TOP + 55, SLIDER_WIDTH, SLIDER_HEIGHT, this, GuiHSBSlider.HSBSliderType.BRIGHTNESS, I18n.format("gui.slider.brightness.tooltip"));

        addButton(hsbSliders[0]);
        addButton(hsbSliders[1]);
        addButton(hsbSliders[2]);

        //Reset/Save
        addButton(tintReset = new IconButton(width - 20, EDIT_PANEL_TOP + 5, IconButton.Icons.UNDO, this::onResetButtonPressed, I18n.format("gui.button.reset")));
        tintReset.active = false;

        //Colour Picker
        addButton(colourPicker = new IconButton(width - 36, EDIT_PANEL_TOP + 5, IconButton.Icons.EYEDROPPER, this::onColourPickerButtonPressed, I18n.format("gui.button.picker.0"), I18n.format("gui.button.picker.1")));
        colourPicker.visible = false;

        updateTints(true);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), 0, 0, 0, this.width, this.height, Colour.DARK_GREY, Colour.DARK_GREY);
        GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), 0,0, EDIT_PANEL_TOP, this.width, this.height, Colour.LIGHT_GRAY, Colour.LIGHT_GRAY); // tint button pos + tint button size + tint button border size
        drawString(matrixStack, this.font, I18n.format("gui.tint"), 5, 3, GuiEditor.TEXT_COLOUR);

        //Editing tint pane
        if (this.currTintEdit >= 0)
        {
            this.hLine(matrixStack, 0, this.width, EDIT_PANEL_TOP, Colour.BLACK);
            drawString(matrixStack, this.font, I18n.format("gui.tint.edit", this.currTintEdit + 1), 5, EDIT_PANEL_TOP + 5, GuiEditor.TEXT_COLOUR);

            drawString(matrixStack, this.font, I18n.format("gui.hex") + ":", 5, EDIT_PANEL_TOP + 21, GuiEditor.TEXT_COLOUR);
            this.hexText.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        // Draw preview of colour hovered over
        if (this.selectingColour)
        {
            final int x = mouseX + COLOUR_PREVIEW_OFFSET;
            final int y = mouseY + COLOUR_PREVIEW_OFFSET;
            final int colour = getColourAtPoint(Minecraft.getInstance().mouseHelper.getMouseX(), Minecraft.getInstance().mouseHelper.getMouseY());
            GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), 0, x, y, x + COLOUR_PREVIEW_SIZE, y + COLOUR_PREVIEW_SIZE, colour, colour);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void onTintButtonPushed(Button button)
    {
        currTintEdit = ((TintButton) button).getTintId();
        currTintColour = tintToArgb(parent.getCurrentOutfitPart().tint[currTintEdit]);
        updateTints(true);
        tintReset.active = false;
        colourPicker.active = true;
    }

    private void onResetButtonPressed(Button button)
    {
        final OutfitPart currentPart = parent.getCurrentOutfitPart();
        if (currentPart == null) return;

        final Optional<Part> basePart = PartRegistry.getPart(currentPart.basePart);
        if (!basePart.isPresent())
        {
            Tails.LOGGER.error("Current outfit part does not have a base part available, this is an invalid state!");
            return;
        }

        currTintColour = tintToArgb(basePart.get().tint[currTintEdit]);
        updateTints(true);
        tintReset.active = false;
    }

    private void onColourPickerButtonPressed(Button button)
    {
        setSelectingColour(true);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers)
    {
        this.hexText.charTyped(codePoint, modifiers);

        try
        {
            //Gets the current colour from the hex text
            if (!Strings.isNullOrEmpty(this.hexText.getText()))
            {
                this.currTintColour = Integer.parseInt(this.hexText.getText(), 16);
            }
        }
        catch (NumberFormatException ignored) { }

        updateTints(false);

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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        if (this.selectingColour && mouseButton == 0)
        {
            this.currTintColour = getColourAtPoint(Minecraft.getInstance().mouseHelper.getMouseX(), Minecraft.getInstance().mouseHelper.getMouseX()); //Ignore alpha
            setSelectingColour(false);
            updateTints(true);
            return true;
        }
        else
        {
            this.hexText.mouseClicked(mouseX, mouseY, mouseButton);
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void onValueChangeHSBSlider(GuiHSBSlider source, double sliderValue)
    {
        if (source == this.rgbSliders[0] || source == this.rgbSliders[1] || source == this.rgbSliders[2])
        {
            this.currTintColour = new Color(
                    (int) (this.rgbSliders[0].getValue() * 255F),
                    (int) (this.rgbSliders[1].getValue() * 255F),
                    (int) (this.rgbSliders[2].getValue() * 255F)).getRGB();
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

        GlStateManager.pixelStore(GL11.GL_PACK_ALIGNMENT, 1);
        GlStateManager.pixelStore(GL11.GL_UNPACK_ALIGNMENT, 1);
        this.pixelBuffer.clear();

        GlStateManager.readPixels((int) x, (int) y, 1, 1, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, this.pixelBuffer);

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
                BufferedImage bufferedImage = ImageIO.read(getMinecraft().getResourceManager().getResource(IconButton.ICONS_TEXTURES).getInputStream());
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
            this.hexText.setText(Integer.toHexString(this.currTintColour));
        }

        //RGB Sliders
        Color c = new Color(currTintColour);
        rgbSliders[0].setValue(c.getRed() / 255F);
        rgbSliders[1].setValue(c.getGreen() / 255F);
        rgbSliders[2].setValue(c.getBlue() / 255F);

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

    @Override
    public boolean onValueChange(GuiSlider slider, Float oldValue, Float newValue)
    {
        return true;
    }

    private class TintButton extends Button
    {
        static final int BTN_SIZE = 20;
        private static final int HIGHLIGHT_COLOR = 0xFFCCC23D;
        private static final int OUTLINE_BORDER = 5;
        private static final int HIGHLIGHT_BORDER = 1;

        private final int tintId;
        private final int defaultColour;

        TintButton(int x, int y, int tintId, int defaultColour, Button.IPressable pressedAction)
        {
            super(x, y, BTN_SIZE, BTN_SIZE, new TranslationTextComponent("gui.tint"), pressedAction);
            this.tintId = tintId;
            this.defaultColour = defaultColour;
        }

        @Override
        public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
        {
            if (currTintEdit == tintId)
            {
                GuiUtils.drawGradientRect(
                        matrixStack.getLast().getMatrix(),
                        0,
                        x - OUTLINE_BORDER,
                        y - OUTLINE_BORDER,
                        x + width + OUTLINE_BORDER,
                        y + height + OUTLINE_BORDER,
                        Colour.LIGHT_GRAY,
                        Colour.LIGHT_GRAY);
                this.hLine(matrixStack, x - OUTLINE_BORDER, x + width + OUTLINE_BORDER - 1, y - OUTLINE_BORDER, Colour.BLACK);
                this.vLine(matrixStack, x - OUTLINE_BORDER, y - OUTLINE_BORDER, y + height + OUTLINE_BORDER, Colour.BLACK);
                this.vLine(matrixStack, x + width + OUTLINE_BORDER - 1, y - OUTLINE_BORDER, y + height + OUTLINE_BORDER, Colour.BLACK);
            }
            else if (this.isHovered)
            {
                GuiUtils.drawGradientRect(
                        matrixStack.getLast().getMatrix(),
                        0,
                        x - HIGHLIGHT_BORDER,
                        y - HIGHLIGHT_BORDER,
                        x + width + HIGHLIGHT_BORDER,
                        y + height + HIGHLIGHT_BORDER,
                        HIGHLIGHT_COLOR,
                        HIGHLIGHT_COLOR);
            }

            final OutfitPart currentPart = parent.getCurrentOutfitPart();
            final int colour = currentPart != null ? tintToArgb(currentPart.tint[tintId]) : defaultColour;
            GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), 0, x, y, x + width, y + height, colour, colour);
        }

        public int getTintId()
        {
            return tintId;
        }
    }
}
