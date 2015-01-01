package kihira.tails.client.gui;

import com.google.common.base.Strings;
import org.jetbrains.annotations.NotNull;

public class Dialog<T extends GuiBase> extends Panel<T> {

    protected boolean dragging;
    private int mouseXStart;
    private int mouseYStart;

    protected String title;

    public Dialog(@NotNull T parent, String title, int left, int top, int width, int height) {
        this(parent, left, top, width, height);
        this.title = title;
    }

    public Dialog(@NotNull T parent, int left, int top, int width, int height) {
        super(parent, left, top, width, height);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
        drawGradientRect(0, 0, width, height, 0xFF808080, 0xFF808080);
        drawGradientRect(1, 12, width - 1, height - 1, 0xFF000000, 0xFF000000);

        if (!Strings.isNullOrEmpty(title)) {
            drawString(fontRendererObj, title, 2, 2, 0xFFFFFFFF);
        }

        super.drawScreen(mouseX, mouseY, p_73863_3_);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        //Only if they grab the top
        if (mouseButton == 0 && mouseY < 12) {
            dragging = true;
            mouseXStart = mouseX;
            mouseYStart = mouseY;
        }
        else {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void mouseMovedOrUp(int mouseX, int mouseY, int mouseButton) {
        if (dragging && mouseButton == 0) {
            dragging = false;
        }
        else {
            super.mouseMovedOrUp(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int mouseButton, long pressTime) {
        if (dragging) {
            if (mouseX != mouseXStart) {
                left -= mouseXStart - mouseX;
                right = left + width;
            }

            if (mouseY != mouseYStart) {
                top -= mouseYStart - mouseY;
                bottom = top + height;
            }

        }
        else {
            super.mouseClickMove(mouseX, mouseY, mouseButton, pressTime);
        }
    }
}
