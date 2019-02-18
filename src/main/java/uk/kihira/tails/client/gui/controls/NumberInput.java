package uk.kihira.tails.client.gui.controls;

import com.google.common.base.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;
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
public class NumberInput extends Gui implements IControl<Float>, ITooltip {
    private static final char[] VALID_CHARS = new char[]{'-', '.', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'}; //todo might need to sort first, need to check
    private static final float SHIFT_MOD = 10f;
    private static final float CTRL_MOD = 0.1f;

    private final int xPos;
    private final int yPos;
    private final int width;
    private final int height = 15;

    private final float min;
    private final float max;
    private final float increment;

    private final int btnWidth = 10;
    private final int btnXPos;
    private final int btnHeight;

    private final DecimalFormat df = new DecimalFormat("###.##");
    private final GuiTextField numInput;
    private float num = 0f;
    private IControlCallback<IControl<Float>, Float> callback;

    static {
        Arrays.sort(VALID_CHARS);
    }

    public NumberInput(int x, int y, int width, float minValue, float maxValue, float increment, @Nullable IControlCallback<IControl<Float>, Float> callback) {
        this.xPos = x;
        this.yPos = y;
        this.width = width;
        this.min = minValue;
        this.max = maxValue;
        this.increment = increment;
        this.callback = callback;
        df.setRoundingMode(RoundingMode.FLOOR);

        numInput = new GuiTextField(0, Minecraft.getInstance().fontRenderer, xPos + 1, yPos + 1, width - btnWidth - 2, height - 2);
        numInput.setValidator(input -> {
            if (input == null) return true;
            int dotCount = 0;
            char[] in = input.toCharArray();
            for (int i = 0; i < in.length; i++) {
                char c = in[i];
                if (c == '-' && i != 0) {
                    return false;
                } else if (c == '.') {
                    dotCount++;
                    if (dotCount > 1) return false;
                } else if (Arrays.binarySearch(VALID_CHARS, c) < 0) {
                    return false;
                }
            }
            return true;
        });
        setValue(0f);

        this.btnXPos = xPos + numInput.width + 2;
        this.btnHeight = height / 2;
    }

    public void draw(int mouseX, int mouseY, float partialTicks) {
        numInput.drawTextField(mouseX, mouseY, partialTicks);
        drawRect(btnXPos, yPos, btnXPos + btnWidth, yPos + btnHeight, 0xFFFFFFFF); // Increment
        drawRect(btnXPos, yPos + btnHeight, btnXPos + btnWidth, yPos + height, 0xAAAAAAFF); // Decrement
    }

    public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
        boolean focused = numInput.isFocused();
        numInput.mouseClicked(mouseX, mouseY, mouseButton);
        // Only update num when input loses focus
        if (focused && !numInput.isFocused()) {
            if (!Strings.isNullOrEmpty(numInput.getText())) setValue(Float.valueOf(numInput.getText()));
            else setValue(0f);
            numInput.setCursorPosition(0);
        }

        float inc = increment;
        if (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) inc *= SHIFT_MOD;
        else if (Keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)) inc *= CTRL_MOD;

        // Increase
        if (GuiBaseScreen.isMouseOver(mouseX, mouseY, xPos + numInput.width, yPos, btnWidth, btnHeight)) {
            setValue(num + inc);
        }
        // Decrease
        else if (GuiBaseScreen.isMouseOver(mouseX, mouseY, xPos + numInput.width, yPos + btnHeight, btnWidth, btnHeight)) {
            setValue(num - inc);
        }
    }

    public void keyTyped(char key, int keycode) {
        numInput.charTyped(key, keycode);
    }

    @Override
    public void setValue(Float newValue) {
        newValue = MathHelper.clamp(newValue, min, max);
        if (callback != null && !callback.onValueChange(this, num, newValue)) return;

        num = newValue;
        numInput.setText(df.format(num));
    }

    @Override
    public Float getValue() {
        return num;
    }

    @Override
    public List<String> getTooltip(int mouseX, int mouseY, float mouseIdleTime) {
        if (mouseIdleTime < .5f) return new ArrayList<>();
        List<String> tooltip = new ArrayList<>();
        tooltip.add(String.format("Click arrows to change by %s", increment));
        tooltip.add(String.format("Hold SHIFT to change by %s, CTRL to change by %s", increment * 10f, increment * 0.1f));
        return tooltip; //todo optimise
    }
}
