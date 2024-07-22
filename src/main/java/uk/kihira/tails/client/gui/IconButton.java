package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import uk.kihira.tails.common.Tails;

import java.util.Arrays;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class IconButton extends ExtendedButton
{
    static final ResourceLocation ICONS_TEXTURES = new ResourceLocation(Tails.MOD_ID, "texture/gui/icons.png");
    private static final int ICON_WIDTH = 16;
    private static final int ICON_HEIGHT = 16;

    final Icons icon;
    private final List<Component> tooltip;

    public IconButton(int x, int y, Icons icon, OnPress pressedAction, Component component, Component... tooltips)
    {
        super(x, y, ICON_WIDTH, ICON_HEIGHT, component, pressedAction);
        this.icon = icon;
        this.tooltip = Arrays.asList(tooltips);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            // TODO int textureOffset = getYImage(this.isHovered);
            var textureOffset = 0;
            graphics.blit(ICONS_TEXTURES, this.getX(), this.getY(), icon.u, icon.v + (textureOffset * ICON_WIDTH), ICON_WIDTH, ICON_HEIGHT);
        }
    }

    public static class IconToggleButton extends IconButton
    {
        public boolean toggled;

        public IconToggleButton(int x, int y, Icons icon, OnPress pressedAction, Component component, Component... tooltips)
        {
            super(x, y, icon, pressedAction, component, tooltips);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            if (this.visible && GuiBaseScreen.isMouseOver(mouseX, mouseY, this.getX(), this.getY(), this.width, this.height))
            {
                this.toggled = !this.toggled;
                return true;
            }
            return false;
        }

        @Override
        public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
        {
            if (visible && toggled)
            {
                graphics.setColor(1.f, 1.f, 1.f, 1.f);
                RenderSystem.enableBlend();
                graphics.blit(ICONS_TEXTURES, this.getX(), this.getY(), icon.u, icon.v + ICON_WIDTH * 2, ICON_WIDTH, ICON_HEIGHT);
            }
            else
            {
                super.renderWidget(graphics, mouseX, mouseY, partialTicks);
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
