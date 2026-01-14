package uk.kihira.tails.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import uk.kihira.tails.client.toast.ToastManager;
import uk.kihira.tails.common.Config;
import uk.kihira.tails.common.OutfitManager;
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
        parent.setActiveOutfitPart(null);
    }

    private void onSaveAllButtonPressed(GuiEventListener button)
    {
        var minecraft = this.minecraft();
        var outfit = parent.getOutfit();
        //Update outfit, set local and send it to the server
        OutfitManager.setOutfitForPlayer(minecraft.getGameProfile().id(), outfit);
        ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 40, 100, /*TextFormatting.GREEN + */"Saved!");
        minecraft.setScreen(null);
    }

    @Override
    protected int contentHeight()
    {
        return this.getHeight();
    }

    @Override
    protected double scrollRate()
    {
        // todo
        return 0;
    }
}
