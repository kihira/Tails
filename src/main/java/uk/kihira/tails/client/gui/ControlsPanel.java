package uk.kihira.tails.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import uk.kihira.tails.client.toast.ToastManager;
import uk.kihira.tails.common.Outfit;
import uk.kihira.tails.common.PartInfo;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;

public class ControlsPanel extends Panel<GuiEditor> {

    @SuppressWarnings("unchecked")
    public ControlsPanel(GuiEditor parent, int left, int top, int right, int bottom) {
        super(parent, left, top, right, bottom);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        //Mode Switch
        buttonList.add(new GuiButton(0, 3, height - 25, 46, 20, I18n.format("gui.button.mode.library")));
        //Reset/Save
        buttonList.add(new GuiButton(1, width/2 - 23, height - 25, 46, 20, I18n.format("gui.button.reset")));
        buttonList.add(new GuiButton(2, width - 49, height - 25, 46, 20, I18n.format("gui.done")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, 0xDD000000);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        Outfit outfit = parent.getOutfit();
        // Mode Switch
        if (button.id == 0) {
            //TODO change parts data when switching? clear libraryinfo panel?
            boolean libraryMode = button.displayString.equals(I18n.format("gui.button.mode.library"));
            parent.partsPanel.enabled = !libraryMode;
            parent.texturePanel.enabled = !libraryMode;
            parent.tintPanel.enabled = !libraryMode;

            parent.libraryInfoPanel.enabled = libraryMode;
            parent.libraryPanel.enabled = libraryMode;
            parent.libraryImportPanel.enabled = libraryMode;

            parent.partsPanel.selectDefaultListEntry();
            parent.libraryPanel.initList();
            parent.libraryInfoPanel.setEntry(null);
            parent.clearCurrTintEdit();
            parent.refreshTintPane();

            if (!libraryMode) {
                Tails.setLocalOutfit(outfit);
            }
            parent.setOutfit(Tails.localOutfit);

            button.displayString = (libraryMode ? I18n.format("gui.button.mode.editor") : I18n.format("gui.button.mode.library"));
        }
        //Reset All
        else if (button.id == 1) {
            parent.partsPanel.selectDefaultListEntry();
            parent.libraryPanel.initList();
            parent.libraryInfoPanel.setEntry(null);
            parent.clearCurrTintEdit();
            parent.refreshTintPane();
            parent.setOutfitPart(null);
        }
        //Save All
        else if (button.id == 2) {
            //Update part info, set local and send it to the server
            Tails.setLocalOutfit(outfit);
            Tails.proxy.setActiveOutfit(mc.player.getPersistentID(), outfit);
            Tails.networkWrapper.sendToServer(new PlayerDataMessage(mc.getSession().getProfile().getId(), outfit, false));
            ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 40, 100, TextFormatting.GREEN + "Saved!");
            this.mc.displayGuiScreen(null);
        }
    }
}
