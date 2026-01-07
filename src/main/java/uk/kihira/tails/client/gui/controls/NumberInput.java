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
import uk.kihira.tails.client.Colour;
import uk.kihira.tails.client.gui.GuiBaseScreen;
import uk.kihira.tails.client.gui.IControl;
import uk.kihira.tails.client.gui.IControlCallback;
import uk.kihira.tails.client.gui.ITooltip;

import javax.annotation.Nullable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// todo tooltips only work for guibuttons
public class NumberInput extends AbstractContainerWidget implements IControl<Float>, ITooltip
{
    private static final char[] VALID_CHARS = new char[]{'-', '.', ',', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
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
    private float value = 0f;
    private IControlCallback<IControl<Float>, Float> callback;

    static
    {
        Arrays.sort(VALID_CHARS);
    }

    public NumberInput(int x, int y, int width, float minValue, float maxValue, float increment, @Nullable IControlCallback<IControl<Float>, Float> callback)
    {
        super(x, y, width, 15, Component.empty());

        this.min = minValue;
        this.max = maxValue;
        this.increment = increment;
        this.callback = callback;
        this.decimalFormat.setRoundingMode(RoundingMode.FLOOR);

        this.numInput = new EditBox(Minecraft.getInstance().font, this.getX() + btnWidth, this.getY(), width - btnWidth, height, Component.empty());
        this.numInput.setFilter(input ->
        {
            int dotCount = 0;
            var in = input.toCharArray();
            for (int i = 0; i < in.length; i++)
            {
                var c = in[i];
                if (c == '-' && i != 0)
                {
                    return false;
                }
                else if (c == '.' || c == ',')
                {
                    dotCount++;
                    if (dotCount > 1) return false;
                }
                else if (Arrays.binarySearch(VALID_CHARS, c) < 0)
                {
                    return false;
                }
            }
            return true;
        });
        setValue(0f);

        this.btnXPos = numInput.getWidth() + 1;
        this.btnHeight = height / 2;
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
        if (GuiBaseScreen.isMouseOver(event.x(), event.y(), this.getX() + this.numInput.getWidth(), this.getY(), this.btnWidth, this.btnHeight))
        {
            setValue(this.value + inc);
        }
        // Decrease
        else if (GuiBaseScreen.isMouseOver(event.x(), event.y(), this.getX() + this.numInput.getWidth(), this.getY() + btnHeight, this.btnWidth, this.btnHeight))
        {
            setValue(this.value - inc);
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
    public void setValue(Float newValue)
    {
        newValue = Math.clamp(newValue, this.min, this.max);
        if (this.callback != null && !this.callback.onValueChange(this, this.value, newValue))
        {
            return;
        }

        this.value = newValue;
        this.numInput.setValue(this.decimalFormat.format(this.value));
    }

    @Override
    public Float getValue()
    {
        return value;
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
