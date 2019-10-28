package uk.kihira.tails.client;

import net.minecraft.client.gui.FontRenderer;

public class ClientUtils {

    /**
     * Draws a string that respects new lines
     * @param fontRenderer Font Renderer
     * @param string Text
     * @param x X Position
     * @param y Y Position
     * @param color Text Colour
     */
    public static void drawStringMultiLine(FontRenderer fontRenderer, String string, int x, int y, int color) {
        String[] lines = string.split("\\\\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            fontRenderer.drawString(line, x, y + (fontRenderer.FONT_HEIGHT * i), color);
        }
    }

    public static void drawCenteredString(FontRenderer fontRenderer, String string, int x, int y, int color) {
        int width = fontRenderer.getStringWidth(string);
        fontRenderer.drawString(string, x - width/2, y, color);
    }
}
