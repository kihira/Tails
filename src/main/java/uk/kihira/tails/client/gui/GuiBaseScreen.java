package uk.kihira.tails.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class GuiBaseScreen extends Screen
{
    private int prevMouseX;
    private int prevMouseY;
    private float mouseIdleTicks;

    protected GuiBaseScreen(Component titleIn)
    {
        super(titleIn);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(graphics, mouseX, mouseY, partialTicks);

        // Tooltips
        for (GuiEventListener btn : this.children())
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

    public static boolean isMouseOver(double mouseX, double mouseY, double x, double y, double width, double height)
    {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }
}
