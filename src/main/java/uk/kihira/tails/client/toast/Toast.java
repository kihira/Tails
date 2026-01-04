package uk.kihira.tails.client.toast;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4fStack;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.List;

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
        this.height = this.message.size() * Minecraft.getInstance().font.lineHeight + 7;
    }

    public void drawToast(GuiGraphics graphics, int mouseX, int mouseY)
    {
        if (this.time > 0)
        {
            Font fontRenderer = Minecraft.getInstance().font;
            mouseOver = mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height;
            int opacity = mouseOver ? 255 : (int) (this.time * 256F / 10F);
            if (opacity > 255) opacity = 255;
            if (mouseOver) time = 20;

            if (opacity > 0)
            {
                //RenderSystem.enableBlend();
                //RenderSystem.disableLighting();
                //RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                drawBackdrop(graphics, xPos, yPos, width, height);
                int colour = 0xFFFFFF | (opacity << 24);
                for (int i = 0; i < message.size(); i++) {
                    String s = message.get(i);
                    graphics.drawString(fontRenderer, s, (xPos + width / 2) - (fontRenderer.width(s) / 2), yPos + 4 + (fontRenderer.lineHeight * i), colour);
                }
                //RenderSystem.disableBlend();
                //RenderSystem.color4f(0F, 0F, 0F, 1F);
            }
        }
    }

    private void drawBackdrop(GuiGraphics graphics, int x, int y, int width, int height)
    {
        int opacity = mouseOver ? 255 : (int) (this.time * 256F / 25F);
        if (opacity > 255) opacity = 255;

        //Black back
        int colour = (opacity << 24);
        graphics.fill(x + 1, y, x + width - 1, y + height, colour);
        graphics.fill(x, y + 1, x + 1, y + height - 1, colour);
        graphics.fill(x + width - 1, y + 1, x + width, y + height - 1, colour);

        //Border
        colour = 0x28025c | (opacity << 24);
        graphics.fill(x + 1, y + 1, x + width - 1, y + 2, colour);
        graphics.fill(x + 1, y + height - 1, x + width - 1, y + height - 2, colour);
        graphics.fill(x + 1, y + 1, x + 2, y + height - 1, colour);
        graphics.fill(x + width - 1, y + 1, x + width - 2, y + height - 1, colour);
    }
}
