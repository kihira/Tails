package kihira.tails.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class GuiBaseScreen extends GuiScreen {

    @Override
    public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
        super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);

        //Tooltips
        for (Object obj : this.buttonList) {
            if (obj instanceof GuiButtonTooltip && ((GuiButtonTooltip) obj).func_146115_a()) ((GuiButtonTooltip) obj).func_146111_b(p_73863_1_, p_73863_2_);
        }
    }

    public class GuiButtonTooltip extends GuiButton {
        private final int maxTextWidth;
        protected final String[] tooltips;

        public GuiButtonTooltip(int id, int x, int y, int width, int height, String text, int maxTextWidth, String... tooltips) {
            super(id, x, y, width, height, text);
            this.maxTextWidth = maxTextWidth;
            this.tooltips = tooltips;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void func_146111_b(int x, int y) {
            if (this.tooltips != null && this.tooltips.length > 0) {
                List<String> list = new ArrayList<String>();
                for (String s : this.tooltips) {
                    list.addAll(fontRendererObj.listFormattedStringToWidth(s, this.maxTextWidth));
                }
                func_146283_a(list, x, y);
            }
        }
    }

    public class GuiButtonToggle extends GuiButtonTooltip {

        public GuiButtonToggle(int id, int x, int y, int width, int height, String text, int maxTextWidth, String... tooltips) {
            super(id, x, y, width, height, text, maxTextWidth, tooltips);
        }

        @Override
        public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
            if (this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height) {
                this.enabled = !this.enabled;
                return true;
            }
            return false;
        }

        @Override
        public void func_146111_b(int x, int y) {
            if (this.tooltips != null && this.tooltips.length > 0) {
                List<String> list = new ArrayList<String>();
                Collections.addAll(list, this.tooltips);
                list.add((!this.enabled ? EnumChatFormatting.GREEN + EnumChatFormatting.ITALIC.toString() + "Enabled" : EnumChatFormatting.RED + EnumChatFormatting.ITALIC.toString() + "Disabled"));
                func_146283_a(list, x, y);
            }
        }
    }
}
