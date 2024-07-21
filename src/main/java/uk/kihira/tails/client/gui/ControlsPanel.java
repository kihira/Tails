package uk.kihira.tails.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.network.PacketDistributor;
import uk.kihira.tails.client.toast.ToastManager;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;

public class ControlsPanel extends Panel<GuiEditor>
{
    ControlsPanel(GuiEditor parent, int left, int top, int right, int bottom)
    {
        super(parent, left, top, right, bottom);
    }

    @Override
    public void init()
    {
        //Reset/Save
        this.addRenderableWidget(new ExtendedButton(this.width / 2 - 23, this.height - 25, 46, 20, Component.translatable("gui.button.reset"), this::onResetAllButtonPressed));
        this.addRenderableWidget(new ExtendedButton(this.width - 49, this.height - 25, 46, 20, Component.translatable("gui.done"), this::onSaveAllButtonPressed));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        guiGraphics.fillGradient(0, 0, 0, this.width, this.height, GuiEditor.DARK_GREY, GuiEditor.DARK_GREY);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    private void onResetAllButtonPressed(GuiEventListener button)
    {
        //parent.partsListWidget.selectDefaultListEntry();
        parent.refreshTintPane();
        parent.setActiveOutfitPart(null);
    }

    private void onSaveAllButtonPressed(GuiEventListener button)
    {
        var outfit = parent.getOutfit();
        //Update part info, set local and send it to the server
        Tails.setLocalOutfit(outfit);
        Tails.proxy.setActiveOutfit(this.getMinecraft().getGameProfile().getId(), outfit);
        PacketDistributor.SERVER.noArg().send(new PlayerDataMessage(this.getMinecraft().getGameProfile().getId(), outfit, false));
        ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 40, 100, /*TextFormatting.GREEN + */"Saved!");
        this.getMinecraft().setScreen(null);
    }
}
