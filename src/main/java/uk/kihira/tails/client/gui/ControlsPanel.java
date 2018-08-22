package uk.kihira.tails.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import uk.kihira.tails.client.toast.ToastManager;
import uk.kihira.tails.common.Outfit;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;

public class ControlsPanel extends Panel<GuiEditor> {
    private static final int MODE_BUTTON_ID = 0;
    private static final int RESET_BUTTON_ID = 1;
    private static final int DONE_BUTTON_ID = 2;

    ControlsPanel(GuiEditor parent, int left, int top, int right, int bottom) {
        super(parent, left, top, right, bottom);
    }

    @Override
    public void initGui() {
        //Mode Switch
        buttonList.add(new GuiButton(MODE_BUTTON_ID, 3, height - 25, 46, 20, I18n.format("gui.button.mode.library")));
        //Reset/Save
        buttonList.add(new GuiButton(RESET_BUTTON_ID, width/2 - 23, height - 25, 46, 20, I18n.format("gui.button.reset")));
        buttonList.add(new GuiButton(DONE_BUTTON_ID, width - 49, height - 25, 46, 20, I18n.format("gui.done")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, GuiEditor.DARK_GREY);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        Outfit outfit = parent.getOutfit();
        // Mode Switch
        if (button.id == MODE_BUTTON_ID) {
            //TODO change parts data when switching? clear libraryinfo panel?
            boolean libraryMode = button.displayString.equals(I18n.format("gui.button.mode.library"));
            parent.partsPanel.enabled = !libraryMode;
            parent.tintPanel.enabled = !libraryMode;

            parent.libraryInfoPanel.enabled = libraryMode;
            parent.libraryPanel.enabled = libraryMode;

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
        else if (button.id == RESET_BUTTON_ID) {
            parent.partsPanel.selectDefaultListEntry();
            parent.libraryPanel.initList();
            parent.libraryInfoPanel.setEntry(null);
            parent.clearCurrTintEdit();
            parent.refreshTintPane();
            parent.setActiveOutfitPart(null);
        }
        //Save All
        else if (button.id == DONE_BUTTON_ID) {
            //Update part info, set local and send it to the server
            Tails.setLocalOutfit(outfit);
            Tails.proxy.setActiveOutfit(mc.player.getPersistentID(), outfit);
            Tails.networkWrapper.sendToServer(new PlayerDataMessage(mc.getSession().getProfile().getId(), outfit, false));
            ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 40, 100, TextFormatting.GREEN + "Saved!");
            this.mc.displayGuiScreen(null);
        }
    }
}
