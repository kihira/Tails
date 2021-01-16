package uk.kihira.tails.client.gui.dialog;

import com.google.common.base.Strings;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraftforge.fml.client.gui.GuiUtils;
import uk.kihira.tails.client.gui.GuiBase;
import uk.kihira.tails.client.gui.Panel;
import org.apache.commons.lang3.Validate;

public class Dialog<T extends GuiBase & IDialogCallback> extends Panel<T>
{
    protected boolean dragging;
    private double mouseXStart;
    private double mouseYStart;

    protected String title;

    public Dialog(T parent, String title, int left, int top, int width, int height)
    {
        this(parent, left, top, width, height);
        this.title = title;
    }

    public Dialog(T parent, int left, int top, int width, int height)
    {
        super(parent, left, top, width, height);
        Validate.isInstanceOf(IDialogCallback.class, parent);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), 0, 0, 0, this.width, this.height, 0xFF808080, 0xFF808080);
        GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), 0, 1, 12, this.width - 1, this.height - 1, 0xFF000000, 0xFF000000);

        if (!Strings.isNullOrEmpty(this.title))
        {
            drawString(matrixStack, this.font, this.title, 2, 2, 0xFFFFFFFF);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        //Only if they grab the top
        if (mouseButton == 0 && mouseY < 12)
        {
            dragging = true;
            mouseXStart = mouseX;
            mouseYStart = mouseY;
            return true;
        }
        else
        {
            return super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

/*    @Override
    TODO public void mouseMovedOrUp(int mouseX, int mouseY, int mouseButton) {
        if (dragging && mouseButton == 0) {
            dragging = false;
        }
        else {
            super.mouseMovedOrUp(mouseX, mouseY, mouseButton);
        }
    }*/

/*    @Override
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
    }*/
}
