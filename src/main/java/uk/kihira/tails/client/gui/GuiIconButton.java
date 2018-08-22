package uk.kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import uk.kihira.tails.common.Tails;

import java.util.Arrays;
import java.util.List;

public class GuiIconButton extends GuiButton implements ITooltip {
    static final ResourceLocation ICONS_TEXTURES = new ResourceLocation(Tails.MOD_ID, "texture/gui/icons.png");
    private static final int ICON_WIDTH = 16;
    private static final int ICON_HEIGHT = 16;

    final Icons icon;
    private final List<String> tooltip;

    public GuiIconButton(int id, int x, int y, Icons icon, String... tooltips) {
        super(id, x, y, ICON_WIDTH, ICON_HEIGHT, "");
        this.icon = icon;
        this.tooltip = Arrays.asList(tooltips);
    }

    @Override
    public void drawButton(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            minecraft.getTextureManager().bindTexture(ICONS_TEXTURES);
            GlStateManager.color(1.f, 1.f, 1.f, 1.f);
            GlStateManager.enableBlend();
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            //Check mouse over
            hovered = GuiBaseScreen.isMouseOver(mouseX, mouseY, x, y, width, height);
            int textureOffset = getHoverState(hovered);

            drawTexturedModalRect(x, y, icon.u, icon.v + (textureOffset * ICON_WIDTH), ICON_WIDTH, ICON_HEIGHT);
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
            if (visible && mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height) {
                toggled = !toggled;
                return true;
            }
            return false;
        }

        @Override
        public void drawButton(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {
            if (visible && toggled) {
                minecraft.getTextureManager().bindTexture(ICONS_TEXTURES);
                GlStateManager.color(1.f, 1.f, 1.f, 1.f);
                GlStateManager.enableBlend();
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                //Check mouse over
                hovered = GuiBaseScreen.isMouseOver(mouseX, mouseY, x, y, width, height);
                drawTexturedModalRect(x, y, icon.u, icon.v + ICON_WIDTH * 2, ICON_WIDTH, ICON_HEIGHT);
            } else {
                super.drawButton(minecraft, mouseX, mouseY, partialTicks);
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
