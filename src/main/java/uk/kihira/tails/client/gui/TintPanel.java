package uk.kihira.tails.client.gui;

import com.google.common.base.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import uk.kihira.tails.client.PartRegistry;
import uk.kihira.tails.client.gui.controls.GuiHSBSlider;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;

public class TintPanel extends Panel<GuiEditor> implements GuiHSBSlider.IHSBSliderCallback, IControlCallback<GuiSlider, Float> {
    private static final int TINT_1_BUTTON = 0;
    private static final int TINT_2_BUTTON = 1;
    private static final int TINT_3_BUTTON = 2;
    private static final int HEX_TEXT = -1;
    private static final int RED_SATURATION_SLIDER = 5;
    private static final int GREEN_SATURATION_SLIDER = 6;
    private static final int BLUE_SATURATION_SLIDER = 7;
    private static final int HUE_SLIDER = 15;
    private static final int SATURATION_SLIDER = 16;
    private static final int BRIGHTNESS_SLIDER = 17;
    private static final int RESET_BUTTON = 8;
    private static final int COLOR_PICKER_BUTTON = 21;

    private static final int SLIDER_WIDTH = 120;
    private static final int SLIDER_HEIGHT = 10;
    private static final int COLOUR_PREVIEW_SIZE = 10;
    private static final int COLOUR_PREVIEW_OFFSET = 8;

    private int currTintEdit = 0;
    private int currTintColour = GuiEditor.TEXT_COLOUR;
    private GuiTextField hexText;
    private GuiHSBSlider[] hsbSliders;
    private GuiHSBSlider[] rgbSliders;
    private GuiIconButton tintReset;
    private GuiIconButton colourPicker;
    private IntBuffer pixelBuffer;
    private boolean selectingColour = false;
    private int editPaneTop;

    TintPanel(GuiEditor parent, int left, int top, int width, int height) {
        super(parent, left, top, width, height);
        alwaysReceiveMouse = true;
    }

    @Override
    public void initGui() {
        editPaneTop = 40;

        // Edit tint buttons
        buttonList.add(new TintButton(TINT_1_BUTTON, 10, 16, TINT_1_BUTTON));
        buttonList.add(new TintButton(TINT_2_BUTTON, 40, 16, TINT_2_BUTTON));
        buttonList.add(new TintButton(TINT_3_BUTTON, 70, 16, TINT_3_BUTTON));

        //Tint edit pane
        hexText = new GuiTextField(HEX_TEXT, fontRenderer, 30, editPaneTop + 20, 73, 10);
        hexText.setMaxStringLength(6);

        //RGB sliders
        rgbSliders = new GuiHSBSlider[3];
        rgbSliders[0] = new GuiHSBSlider(RED_SATURATION_SLIDER, 5, editPaneTop + 70, SLIDER_WIDTH, SLIDER_HEIGHT, this, GuiHSBSlider.HSBSliderType.SATURATION, I18n.format("gui.slider.red.tooltip"));
        rgbSliders[1] = new GuiHSBSlider(GREEN_SATURATION_SLIDER, 5, editPaneTop + 80, SLIDER_WIDTH, SLIDER_HEIGHT, this, GuiHSBSlider.HSBSliderType.SATURATION, I18n.format("gui.slider.green.tooltip"));
        rgbSliders[2] = new GuiHSBSlider(BLUE_SATURATION_SLIDER, 5, editPaneTop + 90, SLIDER_WIDTH, SLIDER_HEIGHT, this, GuiHSBSlider.HSBSliderType.SATURATION, I18n.format("gui.slider.blue.tooltip"));
        rgbSliders[0].setHue(0f);
        rgbSliders[1].setHue(1f / 3f);
        rgbSliders[2].setHue(2f / 3f);

        buttonList.add(rgbSliders[0]);
        buttonList.add(rgbSliders[1]);
        buttonList.add(rgbSliders[2]);

        //HBS sliders
        hsbSliders = new GuiHSBSlider[3];
        hsbSliders[0] = new GuiHSBSlider(HUE_SLIDER, 5, editPaneTop + 35, SLIDER_WIDTH, SLIDER_HEIGHT, this, GuiHSBSlider.HSBSliderType.HUE, I18n.format("gui.slider.hue.tooltip"));
        hsbSliders[1] = new GuiHSBSlider(SATURATION_SLIDER, 5, editPaneTop + 45, SLIDER_WIDTH, SLIDER_HEIGHT, this, GuiHSBSlider.HSBSliderType.SATURATION, I18n.format("gui.slider.saturation.tooltip"));
        hsbSliders[2] = new GuiHSBSlider(BRIGHTNESS_SLIDER, 5, editPaneTop + 55, SLIDER_WIDTH, SLIDER_HEIGHT, this, GuiHSBSlider.HSBSliderType.BRIGHTNESS, I18n.format("gui.slider.brightness.tooltip"));

        buttonList.add(hsbSliders[0]);
        buttonList.add(hsbSliders[1]);
        buttonList.add(hsbSliders[2]);

        //Reset/Save
        buttonList.add(tintReset = new GuiIconButton(RESET_BUTTON, width - 20, editPaneTop + 2, GuiIconButton.Icons.UNDO, I18n.format("gui.button.reset")));
        tintReset.enabled = false;

        //Colour Picker
        buttonList.add(colourPicker = new GuiIconButton(COLOR_PICKER_BUTTON, width - 36, editPaneTop + 1, GuiIconButton.Icons.EYEDROPPER, I18n.format("gui.button.picker.0"), I18n.format("gui.button.picker.1")));
        colourPicker.visible = false;

        updateTints(true);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, GuiEditor.DARK_GREY);
        drawString(fontRenderer, I18n.format("gui.tint"), 5, 3, GuiEditor.TEXT_COLOUR);

        //Editing tint pane
        if (currTintEdit >= 0) {
            drawHorizontalLine(0, width, editPaneTop, GuiEditor.HOZ_LINE_COLOUR);
            fontRenderer.drawString(I18n.format("gui.tint.edit", currTintEdit + 1), 5, editPaneTop + 5, GuiEditor.TEXT_COLOUR);

            fontRenderer.drawString(I18n.format("gui.hex") + ":", 5, editPaneTop + 21, GuiEditor.TEXT_COLOUR);
            hexText.drawTextBox();
        }

        // Draw preview of colour hovered over
        if (selectingColour) {
            int x = mouseX + COLOUR_PREVIEW_OFFSET;
            int y = mouseY + COLOUR_PREVIEW_OFFSET;
            drawRect(x, y, x + COLOUR_PREVIEW_SIZE, y + COLOUR_PREVIEW_SIZE, getColourAtPoint(Mouse.getX(), Mouse.getY()));
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (parent.getCurrentOutfitPart() == null) return;

        // Tint buttons
        if (button.id >= TINT_1_BUTTON && button.id <= TINT_3_BUTTON) {
            currTintEdit = button.id;
            currTintColour = tintToArgb(parent.getCurrentOutfitPart().tint[currTintEdit]);
            updateTints(true);
            tintReset.enabled = false;
            colourPicker.enabled = true;
        }
        // Reset Tint
        else if (button.id == RESET_BUTTON) {
            currTintColour = tintToArgb(PartRegistry.getPart(parent.getCurrentOutfitPart().basePart).tint[currTintEdit]);
            updateTints(true);
            tintReset.enabled = false;
        }
        // Colour Picker
        else if (button.id == COLOR_PICKER_BUTTON) {
            setSelectingColour(true);
        }
    }

    @Override
    public void keyTyped(char letter, int keyCode) {
        hexText.textboxKeyTyped(letter, keyCode);

        try {
            //Gets the current colour from the hex text
            if (!Strings.isNullOrEmpty(hexText.getText())) {
                this.currTintColour = Integer.parseInt(hexText.getText(), 16);
            }
        } catch (NumberFormatException ignored) {
        }

        updateTints(false);

        if (keyCode == Keyboard.KEY_ESCAPE && selectingColour) {
            setSelectingColour(false);
        } else {
            super.keyTyped(letter, keyCode);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (selectingColour && mouseButton == 0) {
            currTintColour = getColourAtPoint(Mouse.getEventX(), Mouse.getEventY()); //Ignore alpha
            setSelectingColour(false);
            updateTints(true);
        } else {
            hexText.mouseClicked(mouseX, mouseY, mouseButton);
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void onValueChangeHSBSlider(GuiHSBSlider source, double sliderValue) {
        if (source == rgbSliders[0] || source == rgbSliders[1] || source == rgbSliders[2]) {
            currTintColour = new Color((int) (rgbSliders[0].getValue() * 255F), (int) (rgbSliders[1].getValue() * 255F), (int) (rgbSliders[2].getValue() * 255F)).getRGB();
        } else {
            float[] hsbvals = {(float) hsbSliders[0].getValue(), (float) hsbSliders[1].getValue(), (float) hsbSliders[2].getValue()};
            hsbvals[source.getType().ordinal()] = (float) sliderValue;
            currTintColour = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]).getRGB();
        }
        updateTints(true);
    }

    /**
     * Converts a vec3 of floats to the ARGB int format used by Minecraft
     * @param tint The tint
     */
    protected int tintToArgb(float[] tint) {
        int col = 0xFF000000;
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
    protected float[] argbToTint(int argb) {
        float[] tint = new float[3];
        tint[0] = ((argb >> 16) & 0xFF) / 255f;
        tint[1] = ((argb >> 8) & 0xFF) / 255f;
        tint[2] = (argb & 0xFF) / 255f;
        return tint;
    }

    private int getColourAtPoint(int x, int y) {
        int[] pixelData;
        int pixels = 1;

        if (pixelBuffer == null) {
            pixelBuffer = BufferUtils.createIntBuffer(pixels);
        }
        pixelData = new int[pixels];

        GlStateManager.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GlStateManager.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        pixelBuffer.clear();

        GlStateManager.glReadPixels(x, y, 1, 1, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);

        pixelBuffer.get(pixelData);

        return pixelData[0];
    }

    private void setSelectingColour(boolean selectingColour) {
        this.selectingColour = selectingColour;

        if (selectingColour) {
            try {
                BufferedImage bufferedImage = ImageIO.read(mc.getResourceManager().getResource(GuiIconButton.ICONS_TEXTURES).getInputStream());
                int[] pixelData;
                int pixels = 16 * 16;
                pixelData = new int[pixels];
                IntBuffer buffer = IntBuffer.wrap(bufferedImage.getRGB(GuiIconButton.Icons.EYEDROPPER.u, GuiIconButton.Icons.EYEDROPPER.v + 16, 16, 16, pixelData, 0, 16));
                org.lwjgl.input.Cursor cursor = new org.lwjgl.input.Cursor(16, 16, 0, 15, 1, buffer, null);

                Mouse.setNativeCursor(cursor);

            } catch (LWJGLException | IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Mouse.setNativeCursor(null);
            } catch (LWJGLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates the controls values for this panel as well as updates the tints for the selected OutfitPart
     *
     * @param updateHex Whether the hex text should be updated
     */
    void updateTints(boolean updateHex) {
        hexText.setTextColor(currTintColour);
        if (updateHex) {
            hexText.setText(Integer.toHexString(currTintColour));
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

        if (currTintEdit >= 0) {
            rgbSliders[0].visible = rgbSliders[1].visible = rgbSliders[2].visible = true;
            hsbSliders[0].visible = hsbSliders[1].visible = hsbSliders[2].visible = true;
            tintReset.visible = true;
            colourPicker.visible = true;
        } else {
            rgbSliders[0].visible = rgbSliders[1].visible = rgbSliders[2].visible = false;
            hsbSliders[0].visible = hsbSliders[1].visible = hsbSliders[2].visible = false;
            tintReset.visible = false;
            colourPicker.visible = false;
        }

        tintReset.enabled = true;

        if (parent.getCurrentOutfitPart() != null) {
            parent.getCurrentOutfitPart().tint[currTintEdit] = argbToTint(currTintColour);
        }
    }

    @Override
    public boolean onValueChange(GuiSlider slider, Float oldValue, Float newValue) {
        return true;
    }

    private class TintButton extends GuiButton {
        static final int BTN_SIZE = 20;
        private static final int HIGHLIGHT_COLOR = 0xFFCCC23D;
        private static final int ACTIVE_COLOR = 0xFFCCC23D;

        private final int tintId;

        TintButton(int buttonId, int x, int y, int tintId) {
            super(buttonId, x, y, BTN_SIZE, BTN_SIZE, I18n.format("gui.tint"));
            this.tintId = tintId;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (parent.getCurrentOutfitPart() == null) return;

            hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

            if (currTintEdit == tintId) {
                drawRect(x - 1, y - 1, x + width + 1, y + height + 1, ACTIVE_COLOR);
            } else if (hovered) {
                drawRect(x - 1, y - 1, x + width + 1, y + height + 1, HIGHLIGHT_COLOR);
            }

            drawRect(x, y, x + width, y + height, tintToArgb(parent.getCurrentOutfitPart().tint[tintId]));
        }
    }
}
