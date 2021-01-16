package uk.kihira.tails.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import uk.kihira.tails.client.toast.ToastManager;
import uk.kihira.tails.client.outfit.Outfit;
import uk.kihira.tails.common.Config;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;
import uk.kihira.tails.common.network.TailsPacketHandler;

public class ControlsPanel extends Panel<GuiEditor>
{
    ControlsPanel(GuiEditor parent, int left, int top, int right, int bottom)
    {
        super(parent, left, top, right, bottom);
    }

    @Override
    public void init()
    {
        //Mode Switch
        this.addButton(new ExtendedButton(3, this.height - 25, 46, 20, new TranslationTextComponent("gui.button.mode.library"), this::onModeSwitchButtonPressed));
        //Reset/Save
        this.addButton(new ExtendedButton(this.width / 2 - 23, this.height - 25, 46, 20, new TranslationTextComponent("gui.button.reset"), this::onResetAllButtonPressed));
        this.addButton(new ExtendedButton(this.width - 49, this.height - 25, 46, 20, new TranslationTextComponent("gui.done"), this::onSaveAllButtonPressed));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        GuiUtils.drawGradientRect(matrixStack.getLast().getMatrix(), 0, 0, 0, this.width, this.height, GuiEditor.DARK_GREY, GuiEditor.DARK_GREY);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void onModeSwitchButtonPressed(Button button)
    {
        Outfit outfit = parent.getOutfit();
        //TODO change parts data when switching? clear libraryinfo panel?
        boolean libraryMode = button.getMessage().equals(new TranslationTextComponent("gui.button.mode.library"));
        parent.partsPanel.enabled = !libraryMode;
        parent.tintPanel.enabled = !libraryMode;

        parent.libraryInfoPanel.enabled = libraryMode;
        parent.libraryPanel.enabled = libraryMode;

        parent.partsPanel.selectDefaultListEntry();
        parent.libraryPanel.initList();
        parent.libraryInfoPanel.setEntry(null);
        parent.refreshTintPane();

        if (!libraryMode)
        {
            Tails.setLocalOutfit(outfit);
        }
        parent.setOutfit(Config.localOutfit.get());

        button.setMessage(new TranslationTextComponent(libraryMode ? "gui.button.mode.editor" : "gui.button.mode.library"));
    }

    private void onResetAllButtonPressed(Button button)
    {
        parent.partsPanel.selectDefaultListEntry();
        parent.libraryPanel.initList();
        parent.libraryInfoPanel.setEntry(null);
        parent.refreshTintPane();
        parent.setActiveOutfitPart(null);
    }

    private void onSaveAllButtonPressed(Button button)
    {
        Outfit outfit = parent.getOutfit();
        //Update part info, set local and send it to the server
        Tails.setLocalOutfit(outfit);
        Tails.proxy.setActiveOutfit(this.minecraft.player.getUniqueID(), outfit);
        TailsPacketHandler.networkWrapper.sendToServer(new PlayerDataMessage(this.minecraft.getSession().getProfile().getId(), outfit, false));
        ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 40, 100, TextFormatting.GREEN + "Saved!");
        this.minecraft.displayGuiScreen(null);
    }
}
