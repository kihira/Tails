package kihira.tails.client.gui.dialog;

import cpw.mods.fml.client.config.GuiButtonExt;
import kihira.tails.client.gui.GuiBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfirmationDialog<T extends GuiBase & IDialogCallback> extends Dialog<T> {

    private final List<String> messageList;

    @SuppressWarnings("unchecked")
    public ConfirmationDialog(@NotNull T parent, String title, final String messageList) {
        super(parent, title, parent.width / 4, parent.height / 4, parent.width / 2, 100);
        this.messageList = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(messageList, (parent.width / 2) - 10);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        setHeight((messageList.size() * 9) + 50);

        buttonList.add(new GuiButtonExt(0, (width / 2) - 52, height - 25, 50, 20, "Cancel"));
        buttonList.add(new GuiButtonExt(1, (width / 2) + 2, height - 25, 50, 20, "Confirm"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        //parent.panels.remove(this); TODO CME
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
        super.drawScreen(mouseX, mouseY, p_73863_3_);

        for (int i = 0; i < messageList.size(); i++) {
            drawCenteredString(fontRendererObj, messageList.get(i), width / 2, 17 + (i * 9), 0xFFFFFFFF);
        }
    }
}
