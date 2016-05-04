/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014
 *
 * See LICENSE for full License
 */
package kihira.tails.client.gui;

import kihira.foxlib.client.gui.GuiBaseScreen;
import kihira.foxlib.client.toast.ToastManager;
import kihira.tails.common.PartInfo;
import kihira.tails.common.PartsData;
import kihira.tails.common.Tails;
import kihira.tails.common.network.PlayerDataMessage;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class ControlsPanel extends Panel<GuiEditor> {

    private GuiButton partTypeButton;

    @SuppressWarnings("unchecked")
    public ControlsPanel(GuiEditor parent, int left, int top, int right, int bottom) {
        super(parent, left, top, right, bottom);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        //Mode Switch
        buttonList.add(new GuiButton(0, 38, height - 25, 45, 20, net.minecraft.util.text.translation.I18n.translateToLocal("gui.button.mode.library")));
        //Reset/Save
        buttonList.add(new GuiButton(1, width - 83, height - 25, 40, 20, I18n.format("gui.button.reset")));
        buttonList.add(new GuiButton(2, width - 43, height - 25, 40, 20, I18n.format("gui.done")));

        //Export
        buttonList.add(new GuiBaseScreen.GuiButtonTooltip(3, (width / 2) - 20, height - 25, 40, 20, I18n.format("gui.button.export"),
                (width / 2) - 20, I18n.format("gui.button.export.0.tooltip")));

        //PartType Select
        buttonList.add(partTypeButton = new GuiButton(4, 3, height - 25, 35, 20, parent.getPartType().name()));
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
            boolean libraryMode = button.displayString.equals(net.minecraft.util.text.translation.I18n.translateToLocal("gui.button.mode.library"));
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

            button.displayString = (libraryMode ? net.minecraft.util.text.translation.I18n.translateToLocal("gui.button.mode.editor") : net.minecraft.util.text.translation.I18n.translateToLocal("gui.button.mode.library"));

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
        //PartType
        else if (button.id == 4) {
            if (parent.getPartType().ordinal() + 1 >= PartsData.PartType.values().length) {
                parent.setPartType(PartsData.PartType.values()[0]);
            }
            else {
                parent.setPartType(PartsData.PartType.values()[parent.getPartType().ordinal() + 1]);
            }

            partTypeButton.displayString = parent.getPartType().name();
        }
    }
}
