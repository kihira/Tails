package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.platform.cursor.CursorType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import uk.kihira.tails.client.*;
import uk.kihira.tails.client.gui.controls.IconButton;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.client.gui.controls.GuiHSBSlider;
import uk.kihira.tails.Tails;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.*;

public class TintPanel extends Panel<OutfitEditScreen> implements GuiHSBSlider.IHSBSliderCallback
{
    private static final int TINT_1_BUTTON_ID = 0;
    private static final int TINT_2_BUTTON_ID = 1;
    private static final int TINT_3_BUTTON_ID = 2;

    private static final int SLIDER_WIDTH = 120;
    private static final int SLIDER_HEIGHT = 10;
    public static final int COLOUR_PREVIEW_SIZE = 10;
    public static final int COLOUR_PREVIEW_OFFSET = 3;

    private static final int EDIT_PANEL_TOP = 43;

    private static CursorType pickerCursor;

    private int currTintEdit = 0;
    private int currTintColour = OutfitEditScreen.TEXT_COLOUR;
    private EditBox hexText;
    private GuiHSBSlider[] hsbSliders;
    private GuiHSBSlider[] rgbSliders;
    private IconButton tintReset;
    private IconButton colourPicker;

    TintPanel(OutfitEditScreen parent, int left, int top, int width, int height)
    {
        super(parent, left, top, width, height);
        this.alwaysReceiveMouse = true;

        // Bootstrap cursor if needs be
        if (TintPanel.pickerCursor == null)
        {
            TintPanel.pickerCursor = new CursorType("tails:picker", createCursor(this.minecraft().getResourceManager()));
        }

        final int tintButtonY = 20;

        // Edit tint buttons
        this.addChild(new TintButton(this.getX() + 10, this.getY() + tintButtonY, TINT_1_BUTTON_ID, Colour.BLUE, this::onTintButtonPushed));
        this.addChild(new TintButton(this.getX() + 40, this.getY() + tintButtonY, TINT_2_BUTTON_ID, Colour.RED, this::onTintButtonPushed));
        this.addChild(new TintButton(this.getX() + 70, this.getY() + tintButtonY, TINT_3_BUTTON_ID, Colour.GREEN, this::onTintButtonPushed));

        //Tint edit panel
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

            //updateTints(false);
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
        //todo RenderSystem.enableBlend();
        graphics.fill(this.getX(), this.getY(), this.getRight(), this.getY() + EDIT_PANEL_TOP, Colour.LIGHT_GRAY);
        graphics.fill(this.getX(), this.getY() + EDIT_PANEL_TOP, this.getRight(), this.getBottom(), Colour.DARK_GREY);
        graphics.drawString(font(), Component.translatable("gui.tint"), this.getX() + 5, this.getY() + 3, OutfitEditScreen.TEXT_COLOUR);

        //Editing tint pane
        if (this.currTintEdit >= 0)
        {
            graphics.hLine(this.getX(), this.getRight(), EDIT_PANEL_TOP, Colour.BLACK);
            graphics.drawString(font(), Component.translatable("gui.tint.edit", this.currTintEdit + 1), this.getX() + 5, this.getY() + EDIT_PANEL_TOP + 5, OutfitEditScreen.TEXT_COLOUR);

            graphics.drawString(font(), Component.translatable("gui.hex").append(":"),  this.getX() + 5,  this.getY() + EDIT_PANEL_TOP + 21, OutfitEditScreen.TEXT_COLOUR);
            this.hexText.setVisible(true);
        }
        else
        {
            this.hexText.setVisible(false);
        }

        // TODO Optimise?
        if (ColourPicker.isPickingColour())
        {
            graphics.requestCursor(pickerCursor);
        }
        else
        {
            graphics.requestCursor(CursorType.DEFAULT);
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
        ColourPicker.startPickingColour(colour ->
        {
            this.currTintColour = colour;
            updateTints(true);
        });
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean scrolling)
    {
        this.hexText.mouseClicked(event, scrolling);
        return super.mouseClicked(event, scrolling);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
    {

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
        //updateTints(true);
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
            this.hexText.setValue(HexFormat.of().toHexDigits(this.currTintColour, 6));
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

    private static long createCursor(ResourceManager resourceManager)
    {
        final int cursorSize = 16;
        var buffer = BufferUtils.createByteBuffer(cursorSize * cursorSize * 8);

        try
        {
            var bufferedImage = ImageIO.read(resourceManager.getResource(IconButton.ICONS_TEXTURES).get().open());
            var pixelData = bufferedImage.getRGB(
                    IconButton.Icons.EYEDROPPER.u,
                    IconButton.Icons.EYEDROPPER.v + cursorSize,
                    cursorSize,
                    cursorSize,
                    null,
                    0,
                    cursorSize);
            buffer.asIntBuffer().put(pixelData);

            var cursorImage = GLFWImage.create();
            cursorImage.set(cursorSize, cursorSize, buffer);

            return GLFW.glfwCreateCursor(cursorImage, 0, 15);
        }
        catch (IOException e)
        {
            Tails.LOGGER.error(e);
        }
        finally
        {
            buffer.clear();
        }

        return -1;
    }

    @Override
    protected int contentHeight()
    {
        //todo
        return 0;
    }

    @Override
    protected double scrollRate()
    {
        //todo
        return 0;
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
        public void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
        {
            if (currTintEdit == tintId)
            {
                graphics.fill(
                        this.getX() - OUTLINE_BORDER,
                        this.getY() - OUTLINE_BORDER,
                        this.getX() + width + OUTLINE_BORDER,
                        this.getY() + height + OUTLINE_BORDER,
                        Colour.DARK_GREY);
                graphics.hLine(this.getX() - OUTLINE_BORDER, this.getRight() + OUTLINE_BORDER - 1, this.getY() - OUTLINE_BORDER, Colour.BLACK);
                graphics.vLine(this.getX() - OUTLINE_BORDER, this.getY() - OUTLINE_BORDER, this.getBottom() + OUTLINE_BORDER, Colour.BLACK);
                graphics.vLine(this.getRight() + OUTLINE_BORDER - 1, this.getY() - OUTLINE_BORDER, this.getBottom() + OUTLINE_BORDER, Colour.BLACK);
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
            graphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), colour);
        }

        public int getTintId()
        {
            return tintId;
        }
    }
}
