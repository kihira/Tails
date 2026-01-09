package uk.kihira.tails.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import uk.kihira.tails.client.toast.ToastManager;
import uk.kihira.tails.common.Config;
import uk.kihira.tails.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;

public class ControlsPanel extends Panel<OutfitEditScreen>
{
    ControlsPanel(OutfitEditScreen parent, int left, int top, int right, int bottom)
    {
        super(parent, left, top, right, bottom);

        //Reset/Save
        addChild(new ExtendedButton(this.getX() + 3, this.getBottom() - 25, 46, 20, Component.translatable("gui.button.reset"), this::onResetAllButtonPressed));
        addChild(new ExtendedButton(this.getRight() - 49, this.getBottom() - 25, 46, 20, Component.translatable("gui.done"), this::onSaveAllButtonPressed));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks)
    {
        guiGraphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), OutfitEditScreen.DARK_GREY);

        super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
    }

    @Override
        protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
    {

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
        Config.CONFIG.setLocalOutfit(outfit);
        Tails.proxy.setActiveOutfit(this.minecraft().getGameProfile().id(), outfit);
        ClientPacketDistributor.sendToServer(new PlayerDataMessage(this.minecraft().getGameProfile().id(), outfit, false));
        ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 40, 100, /*TextFormatting.GREEN + */"Saved!");
        this.minecraft().setScreen(null);
    }

    @Override
    protected int contentHeight()
    {
        // todo
        return 0;
    }

    @Override
    protected double scrollRate()
    {
        // todo
        return 0;
    }
}
