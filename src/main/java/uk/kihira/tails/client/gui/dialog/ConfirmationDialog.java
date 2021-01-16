package uk.kihira.tails.client.gui.dialog;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import uk.kihira.tails.client.gui.GuiBase;

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

        addButton(new ExtendedButton((width / 2) - 52, height - 25, 50, 20, new TranslationTextComponent("tails.cancel"),this::onButtonPressed));
        addButton(new ExtendedButton((width / 2) + 2, height - 25, 50, 20, new TranslationTextComponent("tails.confirm") ,this::onButtonPressed));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        for (int i = 0; i < this.messageList.size(); i++)
        {
            drawCenteredString(matrixStack, this.font, this.messageList.get(i), width / 2, 17 + (i * 9), 0xFFFFFFFF);
        }
    }

    private void onButtonPressed(Button button)
    {
        //parent.panels.remove(this); TODO CME
    }
}
