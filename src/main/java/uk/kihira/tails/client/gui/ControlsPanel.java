package uk.kihira.tails.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.kihira.tails.client.toast.ToastManager;
import uk.kihira.tails.common.Outfit;
import uk.kihira.tails.common.OutfitManager;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;

@OnlyIn(Dist.CLIENT)
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
        buttons.add(new GuiButton(MODE_BUTTON_ID, 3, height - 25, 46, 20, I18n.format("gui.button.mode.library")) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);
                //TODO change parts data when switching? clear libraryinfo panel?
                boolean libraryMode = displayString.equals(I18n.format("gui.button.mode.library"));
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

                displayString = (libraryMode ? I18n.format("gui.button.mode.editor") : I18n.format("gui.button.mode.library"));
            }
        });
        //Reset/Save
        buttons.add(new GuiButton(RESET_BUTTON_ID, width/2 - 23, height - 25, 46, 20, I18n.format("gui.button.reset")) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);

                parent.partsPanel.selectDefaultListEntry();
                parent.libraryPanel.initList();
                parent.libraryInfoPanel.setEntry(null);
                parent.clearCurrTintEdit();
                parent.refreshTintPane();
                parent.setActiveOutfitPart(null);
            }
        });
        buttons.add(new GuiButton(DONE_BUTTON_ID, width - 49, height - 25, 46, 20, I18n.format("gui.done")) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                super.onClick(mouseX, mouseY);

                //Update part info, set local and send it to the server
                Tails.setLocalOutfit(outfit);
                OutfitManager.INSTANCE.setActiveOutfit(mc.player.getUniqueID(), outfit);
                Tails.networkWrapper.sendToServer(new PlayerDataMessage(mc.getSession().getProfile().getId(), outfit, false));
                ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 40, 100, TextFormatting.GREEN + "Saved!");
                this.mc.displayGuiScreen(null);
            }
        });
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, GuiEditor.DARK_GREY);

        super.render(mouseX, mouseY, partialTicks);
    }
}
