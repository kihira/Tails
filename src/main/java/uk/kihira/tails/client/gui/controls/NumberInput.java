package uk.kihira.tails.client.gui.controls;

import com.google.common.base.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.joml.Math;
import org.jspecify.annotations.Nullable;
import uk.kihira.tails.client.Colour;
import uk.kihira.tails.client.gui.GuiBaseScreen;
import uk.kihira.tails.client.gui.IControl;
import uk.kihira.tails.client.gui.IControlCallback;
import uk.kihira.tails.client.gui.ITooltip;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

// todo tooltips only work for guibuttons
public class NumberInput extends AbstractContainerWidget implements IControl<Float>, ITooltip
{
    private static final float SHIFT_MOD = 10f;
    private static final float CTRL_MOD = 0.1f;

    private final float min;
    private final float max;
    private final float increment;

    private final int btnWidth = 7;
    private final int btnXPos;
    private final int btnHeight;

    private final DecimalFormat decimalFormat = new DecimalFormat("###.##");
    private final EditBox numInput;
    private final int maxIntPlaces;
    private final int maxDecimalPlaces;
    private IControlCallback<IControl<Float>, Float> callback;

    public NumberInput(int x, int y, int width, float minValue, float maxValue, float increment, int maxIntPlaces, int maxDecimalPlaces, @Nullable IControlCallback<IControl<Float>, Float> callback)
    {
        super(x, y, width, 15, Component.empty());

        this.min = minValue;
        this.max = maxValue;
        this.increment = increment;
        this.maxIntPlaces = maxIntPlaces;
        this.maxDecimalPlaces = maxDecimalPlaces;
        this.callback = callback;
        this.decimalFormat.setRoundingMode(RoundingMode.FLOOR);

        this.numInput = new EditBox(Minecraft.getInstance().font, this.getX(), this.getY(), width - btnWidth, height, Component.empty());
        this.numInput.setMaxLength(maxIntPlaces + maxDecimalPlaces + 2); // +1 for decimal point, +1 for negative sign
        this.numInput.setFilter(this::validateInput);
        this.numInput.setResponder(value ->
        {
        });

        setValue(0f);

        this.btnXPos = numInput.getWidth() - 1;
        this.btnHeight = height / 2;
    }

    // Validates the input string to ensure that is a valid float, and within the specified int/decimal place limits
    // Does not check min/max value limits here, that's enforced on setValue only
    private boolean validateInput(String input)
    {
        // Allow negative sign on its own just for a better user experience
        if (input.length() == 1 && input.startsWith("-"))
        {
            return true;
        }

        try
        {
            if (Strings.isNullOrEmpty(input))
            {
                return true;
            }
            Float.parseFloat(input.replace(',', '.'));
        }
        catch (NumberFormatException e)
        {
            return false;
        }

        String[] split = input.split("[.,]");
        String intPart = split[0];
        String decimalPart = split.length == 2 ? split[1] : "";

        if (intPart.startsWith("-"))
        {
            intPart =  intPart.substring(1);
        }

        return intPart.length() <= this.maxIntPlaces && decimalPart.length() <= this.maxDecimalPlaces;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        this.numInput.renderWidget(graphics, mouseX, mouseY, partialTick);
        graphics.fill(this.getX() + this.btnXPos, this.getY(), this.getX() + this.btnXPos + this.btnWidth, this.getY() + this.btnHeight, Colour.WHITE); // Increment
        graphics.fill(this.getX() + this.btnXPos, this.getY() + this.btnHeight, this.getX() + this.btnXPos + this.btnWidth, this.getBottom(), 0xFFAAAAAA); // Decrement

        graphics.drawString(Minecraft.getInstance().font, "+", this.getX() + this.btnXPos + (this.btnWidth / 4), this.getY(), Colour.BLACK, false);
        graphics.drawString(Minecraft.getInstance().font, "-", this.getX() + this.btnXPos + (this.btnWidth / 4), this.getY() + this.btnHeight + 1, Colour.BLACK, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
    {

    }

    @Override
    public void setFocused(boolean p_313936_)
    {
        this.numInput.setFocused(p_313936_);
        super.setFocused(p_313936_);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean scrolling)
    {
        var focused = this.numInput.isFocused();
        this.numInput.mouseClicked(event, scrolling);

        // Only update num when input loses focus
        if (focused && !this.numInput.isFocused())
        {
            var value = this.numInput.getMessage().getString();
            if (!Strings.isNullOrEmpty(value))
            {
                setValue(Float.valueOf(value));
            }
            else
            {
                setValue(0f);
            }
        }

        var inc = this.increment;
        if (event.hasShiftDown())
        {
            inc *= SHIFT_MOD;
        }
        else if (event.hasControlDown())
        {
            inc *= CTRL_MOD;
        }

        // Increase
        if (GuiBaseScreen.isMouseOver(event.x(), event.y(), this.getX() + this.btnXPos, this.getY(), this.btnWidth, this.btnHeight))
        {
            setValue(this.getValue() + inc);
        }
        // Decrease
        else if (GuiBaseScreen.isMouseOver(event.x(), event.y(), this.getX() + this.btnXPos, this.getY() + btnHeight, this.btnWidth, this.btnHeight))
        {
            setValue(this.getValue() - inc);
        }

        return super.mouseClicked(event, scrolling);
    }

    // TODO is this the best way going forwards? will work fine for now
    @Override
    public List<? extends GuiEventListener> children()
    {
        return List.of(this.numInput);
    }

    @Override
    public boolean charTyped(CharacterEvent event)
    {
        return this.numInput.charTyped(event);
    }

    @Override
    public boolean setValue(Float newValue)
    {
        newValue = Math.clamp(this.min, this.max, newValue);
        var newValueStr = String.format("%1$" + this.maxIntPlaces + "." + this.maxDecimalPlaces + "f" ,newValue);

        if (!this.validateInput(newValueStr))
        {
            return false;
        }
        if (this.callback != null && !this.callback.onValueChange(this, this.getValue(), newValue))
        {
            return false;
        }

        this.numInput.setValue(newValueStr);
        return true;
    }

    @Override
    public Float getValue()
    {
        return Strings.isNullOrEmpty(this.numInput.getValue()) ? 0f : Float.parseFloat(this.numInput.getValue());
    }

    @Override
    public List<String> getTooltip(int mouseX, int mouseY, float mouseIdleTime)
    {
        if (mouseIdleTime < .5f)
        {
            return new ArrayList<>();
        }
        List<String> tooltip = new ArrayList<>();
        tooltip.add(String.format("Click arrows to change by %s", this.increment));
        tooltip.add(String.format("Hold SHIFT to change by %s, CTRL to change by %s", this.increment * 10f, this.increment * 0.1f));
        return tooltip; //todo optimise
    }

    @Override
    protected int contentHeight()
    {
        //TODO
        return 0;
    }

    @Override
    protected double scrollRate()
    {
        //TODO
        return 0;
    }
}
