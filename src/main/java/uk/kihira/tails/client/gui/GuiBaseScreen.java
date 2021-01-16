package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class GuiBaseScreen extends Screen
{
    private int prevMouseX;
    private int prevMouseY;
    private float mouseIdleTicks;

    protected GuiBaseScreen(ITextComponent titleIn)
    {
        super(titleIn);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        // Tooltips
        for (Widget btn : buttons)
        {
            if (btn instanceof ITooltip && btn.isMouseOver(mouseX, mouseY))
            {
                if (prevMouseX == mouseX && prevMouseY == mouseY) mouseIdleTicks += partialTicks;
                else if (mouseIdleTicks > 0f) mouseIdleTicks = 0f;
                // todo drawHoveringText(((ITooltip) btn).getTooltip(mouseX, mouseY, mouseIdleTicks), mouseX, mouseY);
                prevMouseX = mouseX;
                prevMouseY = mouseY;
                break;
            }
        }
    }

    public static class GuiButtonToggle extends Button
    {
        public GuiButtonToggle(int x, int y, int width, int height, ITextComponent text, int maxTextWidth, String... tooltips)
        {
            super(x, y, width, height, text, (a) -> {}, (button, matrixStack, mouseX, mouseY) ->
            {
                if (!button.active)
                {
                    // todo Screen.renderTooltip(matrixStack, Minecraft.getInstance().fontRenderer.trimStringToWidth(new StringTextComponent(tooltips), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
                }
            });
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button)
        {
            if (this.visible && GuiBaseScreen.isMouseOver(mouseX, mouseY, x, y, width, height))
            {
                this.active = !this.active;
                return true;
            }
            return false;
        }

        // TODO
/*        @Override
        public void drawButtonForegroundLayer(int x, int y)
        {
            ArrayList<String> list = new ArrayList<>(this.tooltip);
            list.add((!this.enabled ? TextFormatting.GREEN + TextFormatting.ITALIC.toString() + "Enabled" : TextFormatting.RED + TextFormatting.ITALIC.toString() + "Disabled"));
            drawHoveringText(list, x, y);
        }*/
    }

    public static boolean isMouseOver(double mouseX, double mouseY, double x, double y, double width, double height)
    {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }
}
