package uk.kihira.tails.client.gui;

import com.google.common.base.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.I18n;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import uk.kihira.tails.client.gui.controls.GuiHSBSlider;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.IntBuffer;

public class TintPanel extends Panel<GuiEditor> implements GuiHSBSlider.IHSBSliderCallback, IControlCallback<GuiSlider,Float> {

    private int currTintEdit = 0;
    private int currTintColour = GuiEditor.TEXT_COLOUR;
    private GuiSlider sizeSlider;
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
        //Edit tint buttons
        for (int i = 0; i < 3; i++) {
            buttonList.add(new TintButton(i, 10 + (i*30), 16, i));
        }

        // Size Slider
//        sizeSlider = new GuiSlider(this, 6, 10, topOffset+35, width, 0.5f, 1.5f, 1.f);
//        buttonList.add(sizeSlider);

        //Tint edit pane
        hexText = new GuiTextField(-1, fontRenderer, 30, editPaneTop + 20, 73, 10);
        hexText.setMaxStringLength(6);
        hexText.setText(Integer.toHexString(currTintColour));

        //RGB sliders
        rgbSliders = new GuiHSBSlider[3];
        rgbSliders[0] = new GuiHSBSlider(5, 5, editPaneTop + 70, 100, 10, this, GuiHSBSlider.HSBSliderType.SATURATION, I18n.format("gui.slider.red.tooltip"));
        rgbSliders[1] = new GuiHSBSlider(6, 5, editPaneTop + 80, 100, 10, this, GuiHSBSlider.HSBSliderType.SATURATION, I18n.format("gui.slider.green.tooltip"));
        rgbSliders[2] = new GuiHSBSlider(7, 5, editPaneTop + 90, 100, 10, this, GuiHSBSlider.HSBSliderType.SATURATION, I18n.format("gui.slider.blue.tooltip"));
        rgbSliders[0].setHue(0f);
        rgbSliders[1].setHue(1f/3f);
        rgbSliders[2].setHue(2f/3f);

        buttonList.add(rgbSliders[0]);
        buttonList.add(rgbSliders[1]);
        buttonList.add(rgbSliders[2]);

        //HBS sliders
        hsbSliders = new GuiHSBSlider[3];
        hsbSliders[0] = new GuiHSBSlider(15, 5, editPaneTop + 35, 100, 10, this, GuiHSBSlider.HSBSliderType.HUE, I18n.format("gui.slider.hue.tooltip"));
        hsbSliders[1] = new GuiHSBSlider(16, 5, editPaneTop + 45, 100, 10, this, GuiHSBSlider.HSBSliderType.SATURATION, I18n.format("gui.slider.saturation.tooltip"));
        hsbSliders[2] = new GuiHSBSlider(17, 5, editPaneTop + 55, 100, 10, this, GuiHSBSlider.HSBSliderType.BRIGHTNESS, I18n.format("gui.slider.brightness.tooltip"));

        buttonList.add(hsbSliders[0]);
        buttonList.add(hsbSliders[1]);
        buttonList.add(hsbSliders[2]);

        //Reset/Save
        buttonList.add(tintReset = new GuiIconButton(8, width - 20, editPaneTop + 2, GuiIconButton.Icons.UNDO, I18n.format("gui.button.reset")));
        tintReset.enabled = false;

        //Colour Picker
        buttonList.add(colourPicker = new GuiIconButton(21, width - 36, editPaneTop + 1, GuiIconButton.Icons.EYEDROPPER, I18n.format("gui.button.picker.0"), I18n.format("gui.button.picker.1")));
        colourPicker.visible = false;

        refreshTintPane();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, 0xCC000000);
        drawString(fontRenderer, I18n.format("gui.tint"), 5, 3, GuiEditor.TEXT_COLOUR);

        //Editing tint pane
        if (currTintEdit >= 0) {
            drawHorizontalLine(0, width, editPaneTop, 0xFF000000);
            fontRenderer.drawString(I18n.format("gui.tint.edit", currTintEdit+1), 5, editPaneTop + 5, GuiEditor.TEXT_COLOUR);

            fontRenderer.drawString(I18n.format("gui.hex") + ":", 5, editPaneTop + 21, GuiEditor.TEXT_COLOUR);
            hexText.drawTextBox();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (parent.getCurrentOutfitPart() == null) return;

        // Tint buttons
        if (button.id >= 0 && button.id < 3) {
            currTintEdit = button.id;
            //todo currTintColour = parent.getCurrentOutfitPart().tint[currTintEdit] & GuiEditor.TEXT_COLOUR; //Ignore the alpha bits
            hexText.setText(Integer.toHexString(currTintColour));
            refreshTintPane();
            tintReset.enabled = false;
            colourPicker.enabled = true;
        }
        // Reset Tint
        else if (button.id == 8) {
            //todo currTintColour = PartRegistry.getPart(parent.getCurrentOutfitPart().basePart).tint[currTintEdit - 1] & GuiEditor.TEXT_COLOUR; //Ignore the alpha bits
            hexText.setText(Integer.toHexString(currTintColour));
            refreshTintPane();
            tintReset.enabled = false;
        }
        // Colour Picker
        else if (button.id == 21) {
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
        } catch (NumberFormatException ignored) {}

        refreshTintPane();

        if (keyCode == Keyboard.KEY_ESCAPE && selectingColour) {
            setSelectingColour(false);
        }
        else {
            super.keyTyped(letter, keyCode);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (selectingColour && mouseButton == 0) {
            currTintColour = getColourAtPoint(Mouse.getEventX(), Mouse.getEventY()); //Ignore alpha
            setSelectingColour(false);
            refreshTintPane();
        }
        else {
            hexText.mouseClicked(mouseX, mouseY, mouseButton);
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void onValueChangeHSBSlider(GuiHSBSlider source, double sliderValue) {
        if (source == rgbSliders[0] || source == rgbSliders[1] || source == rgbSliders[2]) {
            currTintColour = new Color((int) (rgbSliders[0].getValue() * 255F), (int) (rgbSliders[1].getValue() * 255F), (int) (rgbSliders[2].getValue() * 255F)).getRGB();
        }
        else {
            float[] hsbvals = { (float)hsbSliders[0].getValue(), (float)hsbSliders[1].getValue(), (float)hsbSliders[2].getValue() };
            hsbvals[source.getType().ordinal()] = (float)sliderValue;
            currTintColour = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]).getRGB();
        }
        hexText.setText(Integer.toHexString(currTintColour));
        refreshTintPane();
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
        TextureUtil.processPixelValues(pixelData, 1, 1);

        return pixelData[0] & GuiEditor.TEXT_COLOUR;
    }

    private void setSelectingColour(boolean selectingColour) {
        this.selectingColour = selectingColour;

        if (selectingColour) {
            try {
                BufferedImage bufferedImage = ImageIO.read(mc.getResourceManager().getResource(GuiIconButton.iconsTextures).getInputStream());
                int[] pixelData;
                int pixels = 16 * 16;
                pixelData = new int[pixels];
                IntBuffer buffer = IntBuffer.wrap(bufferedImage.getRGB(GuiIconButton.Icons.EYEDROPPER.u, GuiIconButton.Icons.EYEDROPPER.v + 16, 16, 16, pixelData, 0, 16));
                org.lwjgl.input.Cursor cursor = new org.lwjgl.input.Cursor(16, 16, 0, 15, 1, buffer, null);

                Mouse.setNativeCursor(cursor);

            } catch (LWJGLException | IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                Mouse.setNativeCursor(null);
            } catch (LWJGLException e) {
                e.printStackTrace();
            }
        }
    }

    void refreshTintPane() {
        hexText.setTextColor(currTintColour);

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

        //todo if (currTintEdit >= 0) parent.getCurrentOutfitPart().tint[currTintEdit -1] = currTintColour | 0xFF << 24; //Add the alpha manually
        parent.setActiveOutfitPart(parent.getCurrentOutfitPart());
    }

    @Override
    public boolean onValueChange(GuiSlider slider, Float oldValue, Float newValue) {
        return true;
    }

    @ParametersAreNonnullByDefault
    private class TintButton extends GuiButton {
        private static final int BTN_SIZE = 20;
        private static final int HIGHLIGHT = 0xFFCCC23D;
        private static final int ACTIVE = 0xFFCCC23D;

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
                drawRect(x-1, y-1, x+width+1, y+height+1, ACTIVE);
            }
            else if (hovered) {
                drawRect(x-1, y-1, x+width+1, y+height+1, HIGHLIGHT);
            }

            //todo drawRect(x, y, x + width, y + height, parent.getCurrentOutfitPart().tint[tintId]);

        }
    }
}
