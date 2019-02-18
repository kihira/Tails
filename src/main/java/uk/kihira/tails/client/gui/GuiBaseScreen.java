package uk.kihira.tails.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class GuiBaseScreen extends GuiScreen {
    private int prevMouseX;
    private int prevMouseY;
    private float mouseIdleTicks;

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        // Tooltips
        for (GuiButton btn : buttons) {
            if (btn instanceof ITooltip && btn.isMouseOver()) {
                if (prevMouseX == mouseX && prevMouseY == mouseY) mouseIdleTicks += partialTicks;
                else if (mouseIdleTicks > 0f) mouseIdleTicks = 0f;
                drawHoveringText(((ITooltip) btn).getTooltip(mouseX, mouseY, mouseIdleTicks), mouseX, mouseY);
                prevMouseX = mouseX;
                prevMouseY = mouseY;
                break;
            }
        }
    }

    public class GuiButtonTooltip extends GuiButtonExt implements ITooltip {
        private final int maxTextWidth;
        protected final ArrayList<String> tooltip = new ArrayList<>();

        public GuiButtonTooltip(int id, int x, int y, int width, int height, String text, int maxTextWidth, String... tooltips) {
            super(id, x, y, width, height, text);
            this.maxTextWidth = maxTextWidth;
            if (tooltips != null && tooltips.length > 0) {
                for (String s : tooltips) {
                    tooltip.addAll(fontRenderer.listFormattedStringToWidth(s, this.maxTextWidth));
                }
            }
        }

        @Override
        public List<String> getTooltip(int mouseX, int mouseY, float mouseIdleTime) {
            return this.tooltip;
        }
    }

    public class GuiButtonToggle extends GuiButtonTooltip {

        public GuiButtonToggle(int id, int x, int y, int width, int height, String text, int maxTextWidth, String... tooltips) {
            super(id, x, y, width, height, text, maxTextWidth, tooltips);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            if (this.visible && GuiBaseScreen.isMouseOver(mouseX, mouseY, x, y, width, height)) {
                this.enabled = !this.enabled;
            }
        }

        @Override
        public void drawButtonForegroundLayer(int x, int y) {
            ArrayList<String> list = new ArrayList<>(this.tooltip);
            list.add((!this.enabled ? TextFormatting.GREEN + TextFormatting.ITALIC.toString() + "Enabled" : TextFormatting.RED + TextFormatting.ITALIC.toString() + "Disabled"));
            drawHoveringText(list, x, y);
        }
    }

    public static boolean isMouseOver(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
    }
}
