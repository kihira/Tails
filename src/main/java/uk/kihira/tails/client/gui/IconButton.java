package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.lwjgl.opengl.GL11;
import uk.kihira.tails.common.Tails;

import java.util.Arrays;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class IconButton extends Button
{
    static final ResourceLocation ICONS_TEXTURES = new ResourceLocation(Tails.MOD_ID, "texture/gui/icons.png");
    private static final int ICON_WIDTH = 16;
    private static final int ICON_HEIGHT = 16;

    final Icons icon;
    private final List<String> tooltip;

    public IconButton(int x, int y, Icons icon, Button.IPressable pressedAction, String... tooltips)
    {
        super(x, y, ICON_WIDTH, ICON_HEIGHT, new StringTextComponent(""), pressedAction);
        this.icon = icon;
        this.tooltip = Arrays.asList(tooltips);
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            Minecraft.getInstance().getTextureManager().bindTexture(ICONS_TEXTURES);
            GlStateManager.color4f(1.f, 1.f, 1.f, 1.f);
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

            int textureOffset = getYImage(this.isHovered);

            GuiUtils.drawTexturedModalRect(matrixStack, x, y, icon.u, icon.v + (textureOffset * ICON_WIDTH), ICON_WIDTH, ICON_HEIGHT, 0);
        }
    }

    public static class IconToggleButton extends IconButton
    {
        public boolean toggled;

        public IconToggleButton(int x, int y, Icons icon, Button.IPressable pressedAction, String... tooltips)
        {
            super(x, y, icon, pressedAction, tooltips);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            if (this.visible && GuiBaseScreen.isMouseOver(mouseX, mouseY, this.x, this.y, this.width, this.height))
            {
                this.toggled = !this.toggled;
                return true;
            }
            return false;
        }

        @Override
        public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
        {
            if (visible && toggled)
            {
                Minecraft.getInstance().getTextureManager().bindTexture(ICONS_TEXTURES);
                GlStateManager.color4f(1.f, 1.f, 1.f, 1.f);
                GlStateManager.enableBlend();
                GlStateManager.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                GuiUtils.drawTexturedModalRect(matrixStack, x, y, icon.u, icon.v + ICON_WIDTH * 2, ICON_WIDTH, ICON_HEIGHT, 0);
            }
            else
            {
                super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
            }
        }
    }

    public enum Icons
    {
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

        Icons(int u, int v)
        {
            this.u = u;
            this.v = v;
        }
    }
}
