package uk.kihira.tails.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class ClientUtils
{
    /**
     * Draws a string that respects new lines
     * @param fontRenderer Font Renderer
     * @param string Text
     * @param x X Position
     * @param y Y Position
     * @param color Text Colour
     */
    public static void drawStringMultiLine(GuiGraphics guiGraphics, Font fontRenderer, String string, int x, int y, int color)
    {
        String[] lines = string.split("\\\\n");
        for (int i = 0; i < lines.length; i++) 
        {
            String line = lines[i];
            guiGraphics.drawString(fontRenderer, line, x, y + (fontRenderer.lineHeight * i), color);
        }
    }
}
