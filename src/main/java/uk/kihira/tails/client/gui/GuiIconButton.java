/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira)
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public class GuiIconButton extends GuiButton implements ITooltip {

    public static final ResourceLocation iconsTextures = new ResourceLocation("tails", "texture/gui/icons.png");

    protected final Icons icon;
    private final List<String> tooltip;

    public GuiIconButton(int id, int x, int y, Icons icon, String ... tooltips) {
        super(id, x, y, 16 ,16, "");
        this.icon = icon;
        this.tooltip = Arrays.asList(tooltips);
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        if (visible) {
            minecraft.getTextureManager().bindTexture(iconsTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableBlend();
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);

            //Check mouse over
            hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
            int textureOffset = getHoverState(hovered);

            drawTexturedModalRect(xPosition, yPosition, icon.u, icon.v + (textureOffset * 16), 16, 16);
        }
    }

    public void setHover(boolean hover) {
        hovered = hover;
    }

    @Override
    public List<String> getTooltip(int mouseX, int mouseY, float mouseIdleTime) {
        return tooltip;
    }

    public static class GuiIconToggleButton extends GuiIconButton {

        public boolean toggled;

        public GuiIconToggleButton(int id, int x, int y, Icons icon, String... tooltips) {
            super(id, x, y, icon, tooltips);
        }

        @Override
        public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
            if (this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height) {
                this.toggled = !this.toggled;
                return true;
            }
            return false;
        }

        @Override
        public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
            if (visible && toggled) {
                minecraft.getTextureManager().bindTexture(iconsTextures);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.enableBlend();
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                //Check mouse over
                hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
                drawTexturedModalRect(xPosition, yPosition, icon.u, icon.v + 32, 16, 16);
            }
            else {
                super.drawButton(minecraft, mouseX, mouseY);
            }
        }
    }

    public enum Icons {
        UNDO(0, 0),
        QUESTION(16, 0),
        EYEDROPPER(32, 0),
        SAVE(48, 0),
        DELETE(64, 0),
        COPY(80, 0),
        STAR(96, 0),
        EDIT(112, 0),
        UPLOAD(128, 0),
        DOWNLOAD(144, 0),
        SEARCH(160, 0),
        SERVER(176, 0),
        IMPORT(192, 0),
        EXPORT(208, 0);

        public final int u;
        public final int v;

        Icons(int u, int v) {
            this.u = u;
            this.v = v;
        }
    }
}
