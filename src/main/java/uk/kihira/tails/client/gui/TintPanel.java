package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.platform.cursor.CursorType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jspecify.annotations.Nullable;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import uk.kihira.tails.client.*;
import uk.kihira.tails.client.gui.controls.IconButton;
import uk.kihira.tails.client.outfit.OutfitPart;
import uk.kihira.tails.client.gui.controls.GuiHSBSlider;
import uk.kihira.tails.Tails;
import uk.kihira.tails.client.outfit.Tint;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Semaphore;

public class TintPanel extends Panel<OutfitEditScreen> implements GuiHSBSlider.IHSBSliderCallback, IOutfitPartSelected
{
    private static CursorType pickerCursor;

    private int currentTintIndex = -1;
    private Tint currentTint = Tint.fromARGB(OutfitEditScreen.TEXT_COLOUR);
    private final Semaphore tintUpdateSemaphore = new Semaphore(1); // Feels like a bodge but preventing stack overflows from infinite loops

    private final EditBox hexText;
    private final GuiHSBSlider[] hsbSliders;
    private final GuiHSBSlider[] rgbSliders;
    private final IconButton tintReset;
    private final IconButton colourPicker;

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
        addChild(new TintButton(this.getX() + 10, this.getY() + tintButtonY, TINT_1_BUTTON_ID, Colour.BLUE, this::onTintButtonPushed));
        addChild(new TintButton(this.getX() + 40, this.getY() + tintButtonY, TINT_2_BUTTON_ID, Colour.RED, this::onTintButtonPushed));
        addChild(new TintButton(this.getX() + 70, this.getY() + tintButtonY, TINT_3_BUTTON_ID, Colour.GREEN, this::onTintButtonPushed));

        //Tint edit panel
        addChild(hexText = new EditBox(this.font(), this.getX() + 30, this.getY() + EDIT_PANEL_TOP + 20, 73, 10, Component.empty()));
        this.hexText.setMaxLength(6);
        this.hexText.setValue(currentTint.toHexString());
        this.hexText.setResponder(value ->
        {
            try
            {
                if (value.length() == 6)
                {
                    setCurrentTint(Integer.parseInt(value, 16), false);
                }
            }
            catch (NumberFormatException ignored) { }
        });
        this.hexText.addFormatter((String text, int displayPos) -> FormattedCharSequence.forward(text, Style.EMPTY.withColor(this.currentTint.toARGB())));

        //RGB sliders
        this.rgbSliders = new GuiHSBSlider[3];
        addChild(this.rgbSliders[0] = new GuiHSBSlider(this.getX() + 5, this.getY() + EDIT_PANEL_TOP + 70, SLIDER_WIDTH, SLIDER_HEIGHT, 255, 1, this, GuiHSBSlider.HSBSliderType.SATURATION));
        addChild(this.rgbSliders[1] = new GuiHSBSlider(this.getX() + 5, this.getY() + EDIT_PANEL_TOP + 80, SLIDER_WIDTH, SLIDER_HEIGHT, 255, 1, this, GuiHSBSlider.HSBSliderType.SATURATION));
        addChild(this.rgbSliders[2] = new GuiHSBSlider(this.getX() + 5, this.getY() + EDIT_PANEL_TOP + 90, SLIDER_WIDTH, SLIDER_HEIGHT, 255, 1, this, GuiHSBSlider.HSBSliderType.SATURATION));
        this.rgbSliders[0].setHue(0f);
        this.rgbSliders[1].setHue(1f / 3f);
        this.rgbSliders[2].setHue(2f / 3f);

        //HBS sliders
        this.hsbSliders = new GuiHSBSlider[3];
        addChild(this.hsbSliders[0] = new GuiHSBSlider(this.getX() + 5, this.getY() + EDIT_PANEL_TOP + 35, SLIDER_WIDTH, SLIDER_HEIGHT, 1f, 0f, this, GuiHSBSlider.HSBSliderType.HUE));
        addChild(this.hsbSliders[1] = new GuiHSBSlider(this.getX() + 5, this.getY() + EDIT_PANEL_TOP + 45, SLIDER_WIDTH, SLIDER_HEIGHT, 1f, 0f, this, GuiHSBSlider.HSBSliderType.SATURATION));
        addChild(this.hsbSliders[2] = new GuiHSBSlider(this.getX() + 5, this.getY() + EDIT_PANEL_TOP + 55, SLIDER_WIDTH, SLIDER_HEIGHT, 1f, 0f, this, GuiHSBSlider.HSBSliderType.BRIGHTNESS));

        //Reset/Save
        addChild(this.tintReset = new IconButton(this.getRight() - 20, this.getY() + EDIT_PANEL_TOP + 5, IconButton.Icons.UNDO, this::onResetButtonPressed, Component.translatable("tails.gui.button.reset")));
        this.tintReset.active = false;
        this.tintReset.setTooltip(Tooltip.create(Component.translatable("tails.gui.button.tint_reset.tooltip")));

        //Colour Picker
        addChild(this.colourPicker = new IconButton(this.getRight() - 36, this.getY() + EDIT_PANEL_TOP + 5, IconButton.Icons.EYEDROPPER, this::onColourPickerButtonPressed, Component.translatable("tails.gui.button.picker"), Component.translatable("tails.gui.button.picker.tooltip")));
        this.colourPicker.visible = false;
        this.colourPicker.setTooltip(Tooltip.create(Component.translatable("tails.gui.button.picker.tooltip")));

        setCurrentTintIndex(TINT_INDEX_NONE);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        graphics.fill(this.getX(), this.getY(), this.getRight(), this.getY() + EDIT_PANEL_TOP, Colour.LIGHT_GRAY);
        graphics.fill(this.getX(), this.getY() + EDIT_PANEL_TOP, this.getRight(), this.getBottom(), Colour.DARK_GREY);
        graphics.drawString(font(), Component.translatable("tails.gui.tint"), this.getX() + 5, this.getY() + 3, OutfitEditScreen.TEXT_COLOUR);

        //Editing tint pane
        if (this.currentTintIndex != TINT_INDEX_NONE)
        {
            graphics.hLine(this.getX(), this.getRight(), EDIT_PANEL_TOP, Colour.BLACK);
            graphics.drawString(font(), Component.translatable("tails.gui.tint.edit", this.currentTintIndex + 1), this.getX() + 5, this.getY() + EDIT_PANEL_TOP + 5, OutfitEditScreen.TEXT_COLOUR);

            graphics.drawString(font(), Component.translatable("tails.gui.hex").append(":"),  this.getX() + 5,  this.getY() + EDIT_PANEL_TOP + 21, OutfitEditScreen.TEXT_COLOUR);
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
        setCurrentTintIndex(((TintButton) button).getTintId());
        this.tintReset.active = false;
        this.colourPicker.active = true;
    }

    private void onResetButtonPressed(GuiEventListener button)
    {
        final OutfitPart currentPart = this.parent.getCurrentOutfitPart();
        if (currentPart == null) return;

        final Optional<Part> basePart = PartRegistry.getPart(currentPart.basePart);
        if (basePart.isEmpty())
        {
            Tails.LOGGER.error("Current outfit part does not have a base part available, this is an invalid state!");
            return;
        }

        setCurrentTint(basePart.get().tint[this.currentTintIndex], true);
        this.tintReset.active = false;
    }

    private void onColourPickerButtonPressed(GuiEventListener button)
    {
        ColourPicker.startPickingColour(colour ->
        {
            setCurrentTint(colour, true);
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
        int newColor;
        if (source == this.rgbSliders[0] || source == this.rgbSliders[1] || source == this.rgbSliders[2])
        {
            newColor = new Color(
                    this.rgbSliders[0].getValueInt(),
                    this.rgbSliders[1].getValueInt(),
                    this.rgbSliders[2].getValueInt()).getRGB();
        }
        else
        {
            float[] hsbvals = {(float) this.hsbSliders[0].getValue(), (float) this.hsbSliders[1].getValue(), (float) this.hsbSliders[2].getValue()};
            hsbvals[source.getType().ordinal()] = (float) sliderValue;
            newColor = Color.getHSBColor(hsbvals[0], hsbvals[1], hsbvals[2]).getRGB();
        }

        setCurrentTint(newColor, true);
    }

    private void setCurrentTintIndex(int index)
    {
        this.currentTintIndex = index;

        if (this.currentTintIndex != TINT_INDEX_NONE && this.parent.getCurrentOutfitPart() != null)
        {
            setCurrentTint(this.parent.getCurrentOutfitPart().getTint(this.currentTintIndex), true);
        }
    }

    private void setCurrentTint(int colour, boolean updateHex)
    {
        setCurrentTint(Tint.fromARGB(colour), updateHex);
    }

    /**
     * Updates the controls values for this panel as well as updates the tints for the selected OutfitPart
     *
     * @param updateHex Whether the hex text should be updated
     */
    private void setCurrentTint(Tint tint, boolean updateHex)
    {
        if (!this.tintUpdateSemaphore.tryAcquire())
        {
            return;
        }

        this.currentTint = tint;

        if (updateHex)
        {
            this.hexText.setValue(this.currentTint.toHexString());
        }

        //RGB Sliders
        this.rgbSliders[0].setValue(this.currentTint.redInt());
        this.rgbSliders[1].setValue(this.currentTint.greenInt());
        this.rgbSliders[2].setValue(this.currentTint.blueInt());

        //HSB Sliders
        float[] hsbvals = Color.RGBtoHSB(this.currentTint.redInt(), this.currentTint.greenInt(), this.currentTint.blueInt(), null);
        this.hsbSliders[0].setValue(hsbvals[0]);
        this.hsbSliders[1].setValue(hsbvals[1]);
        this.hsbSliders[2].setValue(hsbvals[2]);
        //The saturation slider needs to know the value of the other 2 sliders
        this.hsbSliders[1].setHue((float) this.hsbSliders[0].getValue());
        this.hsbSliders[1].setBrightness((float) this.hsbSliders[2].getValue());

        if (this.currentTintIndex >= 0)
        {
            this.rgbSliders[0].visible = this.rgbSliders[1].visible = this.rgbSliders[2].visible = true;
            this.hsbSliders[0].visible = this.hsbSliders[1].visible = this.hsbSliders[2].visible = true;
            this.tintReset.visible = true;
            this.colourPicker.visible = true;
        }
        else
        {
            this.rgbSliders[0].visible = this.rgbSliders[1].visible = this.rgbSliders[2].visible = false;
            this.hsbSliders[0].visible = this.hsbSliders[1].visible = this.hsbSliders[2].visible = false;
            this.tintReset.visible = false;
            this.colourPicker.visible = false;
        }

        this.tintReset.active = true;

        if (this.parent.getCurrentOutfitPart() != null)
        {
            this.parent.getCurrentOutfitPart().setTint(this.currentTintIndex, this.currentTint);
        }

        this.tintUpdateSemaphore.release();
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
        return this.getHeight();
    }

    @Override
    protected double scrollRate()
    {
        //todo
        return 0;
    }

    @Override
    public void onOutfitPartSelected(@Nullable OutfitPart part)
    {
        setCurrentTintIndex(0);
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
            super(x, y, BTN_SIZE, BTN_SIZE, Component.translatable("tails.gui.tint"), pressedAction);
            this.tintId = tintId;
            this.defaultColour = defaultColour;
        }

        @Override
        public void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
        {
            if (TintPanel.this.currentTintIndex == this.tintId)
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

            final var currentPart = parent.getCurrentOutfitPart();
            final var colour = currentPart != null ? currentPart.getTint(this.tintId).toARGB() : this.defaultColour;
            graphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), colour);
        }

        public int getTintId()
        {
            return tintId;
        }
    }

    private static final int TINT_1_BUTTON_ID = 0;
    private static final int TINT_2_BUTTON_ID = 1;
    private static final int TINT_3_BUTTON_ID = 2;

    private static final int SLIDER_WIDTH = 120;
    private static final int SLIDER_HEIGHT = 10;
    private static final int EDIT_PANEL_TOP = 43;

    private static final int TINT_INDEX_NONE = -1;
}
