/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.toast;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraftforge.client.event.MouseEvent;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.List;

public class Toast {

    private final int xPos;
    private final int yPos;
    private final int width;
    private final int height;
    private final List<String> message;
    boolean mouseOver;
    int time;

    public Toast(int xPos, int yPos, int width, int time, String... message) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.time = time;
        this.message = Arrays.asList(message);
        height = this.message.size() * Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 7;
    }

    public void onMouseEvent(MouseEvent mouseEvent) {}

    public void drawToast(int mouseX, int mouseY) {
        if (this.time > 0) {
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
            mouseOver = mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height;
            int opacity = mouseOver ? 255 : (int) (this.time * 256F / 10F);
            if (opacity > 255) opacity = 255;
            if (mouseOver) time = 20;

            if (opacity > 0) {
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.disableLighting();
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                drawBackdrop(xPos, yPos, width, height);
                int colour = 0xFFFFFF | (opacity << 24);
                for (int i = 0; i < message.size(); i++) {
                    String s = message.get(i);
                    fontRenderer.drawStringWithShadow(s, (xPos + width / 2) - (fontRenderer.getStringWidth(s) / 2), yPos + 4 + (fontRenderer.FONT_HEIGHT * i), colour);
                }
                GlStateManager.disableBlend();
                GlStateManager.color(0F, 0F, 0F, 1F);
                GlStateManager.popMatrix();
            }
        }
    }

    private void drawBackdrop(int x, int y, int width, int height) {
        int opacity = mouseOver ? 255 : (int) (this.time * 256F / 25F);
        if (opacity > 255) opacity = 255;

        //Black back
        int colour = (opacity << 24);
        Gui.drawRect(x + 1, y, x + width - 1, y + height, colour);
        Gui.drawRect(x, y + 1, x + 1, y + height - 1, colour);
        Gui.drawRect(x + width - 1, y + 1, x + width, y + height - 1, colour);

        //Border
        colour = 0x28025c | (opacity << 24);
        Gui.drawRect(x + 1, y + 1, x + width - 1, y + 2, colour);
        Gui.drawRect(x + 1, y + height - 1, x + width - 1, y + height - 2, colour);
        Gui.drawRect(x + 1, y + 1, x + 2, y + height - 1, colour);
        Gui.drawRect(x + width - 1, y + 1, x + width - 2, y + height - 1, colour);
    }
}
