package uk.kihira.tails.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;

// todo use IToast and vanilla system?
public class Toast
{
    private final int xPos;
    private final int yPos;
    private final int width;
    private final int height;
    private final List<String> message;
    boolean mouseOver;
    int time;

    public Toast(int xPos, int yPos, int width, int time, String... message)
    {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.time = time;
        this.message = Arrays.asList(message);
        this.height = this.message.size() * Minecraft.getInstance().fontRenderer.FONT_HEIGHT + 7;
    }

    public void drawToast(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        if (this.time > 0)
        {
            FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
            mouseOver = mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height;
            int opacity = mouseOver ? 255 : (int) (this.time * 256F / 10F);
            if (opacity > 255) opacity = 255;
            if (mouseOver) time = 20;

            if (opacity > 0)
            {
                matrixStack.push();
                RenderSystem.enableBlend();
                RenderSystem.disableLighting();
                RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                drawBackdrop(matrixStack, xPos, yPos, width, height);
                int colour = 0xFFFFFF | (opacity << 24);
                for (int i = 0; i < message.size(); i++) {
                    String s = message.get(i);
                    fontRenderer.drawStringWithShadow(matrixStack, s, (xPos + width / 2) - (fontRenderer.getStringWidth(s) / 2), yPos + 4 + (fontRenderer.FONT_HEIGHT * i), colour);
                }
                RenderSystem.disableBlend();
                RenderSystem.color4f(0F, 0F, 0F, 1F);
                matrixStack.pop();
            }
        }
    }

    private void drawBackdrop(MatrixStack matrixStack, int x, int y, int width, int height)
    {
        int opacity = mouseOver ? 255 : (int) (this.time * 256F / 25F);
        if (opacity > 255) opacity = 255;

        //Black back
        int colour = (opacity << 24);
        GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), 0, x + 1, y, x + width - 1, y + height, colour, colour);
        GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), 0, x, y + 1, x + 1, y + height - 1, colour, colour);
        GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), 0, x + width - 1, y + 1, x + width, y + height - 1, colour, colour);

        //Border
        colour = 0x28025c | (opacity << 24);
        GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), 0, x + 1, y + 1, x + width - 1, y + 2, colour, colour);
        GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), 0, x + 1, y + height - 1, x + width - 1, y + height - 2, colour, colour);
        GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), 0, x + 1, y + 1, x + 2, y + height - 1, colour, colour);
        GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), 0, x + width - 1, y + 1, x + width - 2, y + height - 1, colour, colour);
    }
}
