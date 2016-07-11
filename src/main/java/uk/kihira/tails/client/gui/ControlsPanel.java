/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */
package uk.kihira.tails.client.gui;

import uk.kihira.foxlib.client.gui.GuiBaseScreen;
import uk.kihira.foxlib.client.toast.ToastManager;
import uk.kihira.tails.common.PartInfo;
import uk.kihira.tails.common.PartsData;
import uk.kihira.tails.common.Tails;
import uk.kihira.tails.common.network.PlayerDataMessage;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class ControlsPanel extends Panel<GuiEditor> {

    @SuppressWarnings("unchecked")
    public ControlsPanel(GuiEditor parent, int left, int top, int right, int bottom) {
        super(parent, left, top, right, bottom);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        int spacing = (width - 51) / 3;
        //Mode Switch
        buttonList.add(new GuiButton(0, 3, height - 25, 46, 20, I18n.format("gui.button.mode.library")));
        //Reset/Save
        buttonList.add(new GuiButton(1, 3 + spacing * 2, height - 25, 46, 20, I18n.format("gui.button.reset")));
        buttonList.add(new GuiButton(2, 3 + spacing * 3, height - 25, 46, 20, I18n.format("gui.done")));

        //Export
        buttonList.add(new GuiBaseScreen.GuiButtonTooltip(3, 3 + spacing, height - 25, 46, 20, I18n.format("gui.button.export"),
                (width / 2) - 20, I18n.format("gui.button.export.0.tooltip")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, 0xDD000000);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        PartsData partsData = parent.getPartsData();
        //Mode Switch
        if (button.id == 0) {
            //>.> You see nothing!
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
                Tails.setLocalPartsData(parent.getPartsData());
            }
            parent.setPartsData(Tails.localPartsData);

            button.displayString = (libraryMode ? I18n.format("gui.button.mode.editor") : I18n.format("gui.button.mode.library"));
        }
        //Reset All
        else if (button.id == 1) {
            PartInfo partInfo = parent.originalPartInfo.deepCopy();
            parent.partsPanel.selectDefaultListEntry();
            parent.libraryPanel.initList();
            parent.libraryInfoPanel.setEntry(null);
            parent.clearCurrTintEdit();
            parent.refreshTintPane();
            parent.setPartsInfo(partInfo);
        }
        //Save All
        else if (button.id == 2) {
            //Update part info, set local and send it to the server
            Tails.setLocalPartsData(partsData);
            Tails.proxy.addPartsData(mc.thePlayer.getPersistentID(), partsData);
            Tails.networkWrapper.sendToServer(new PlayerDataMessage(mc.getSession().getProfile().getId(), partsData, false));
            ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 40, 100, TextFormatting.GREEN + "Saved!");
            this.mc.displayGuiScreen(null);
        }
        //Export
        else if (button.id == 3) {
            mc.displayGuiScreen(new GuiExport(parent, partsData));
        }
    }
}
