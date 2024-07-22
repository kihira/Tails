/*
package uk.kihira.tails.client.gui.dialog;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import uk.kihira.tails.client.gui.GuiBase;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ConfirmationDialog<T extends GuiBase & IDialogCallback> extends Dialog<T>
{
    private final List<String> messageList;

    public ConfirmationDialog(T parent, String title, final String messageList) {
        super(parent, title, parent.width / 4, parent.height / 4, parent.width / 2, 100);
        this.messageList = new ArrayList<>();
        // todo this.messageList = getMinecraft().fontRenderer.listFormattedStringToWidth(messageList, (parent.width / 2) - 10);
    }

    @Override
    public void init()
    {
        setHeight((messageList.size() * 9) + 50);

        addRenderableWidget(new ExtendedButton((width / 2) - 52, height - 25, 50, 20, Component.translatable("tails.cancel"),this::onButtonPressed));
        addRenderableWidget(new ExtendedButton((width / 2) + 2, height - 25, 50, 20, Component.translatable("tails.confirm") ,this::onButtonPressed));
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        for (int i = 0; i < this.messageList.size(); i++)
        {
            guiGraphics.drawCenteredString(this.font, this.messageList.get(i), width / 2, 17 + (i * 9), 0xFFFFFFFF);
        }
    }

    private void onButtonPressed(GuiEventListener button)
    {
        //parent.panels.remove(this); TODO CME
    }
}
*/
