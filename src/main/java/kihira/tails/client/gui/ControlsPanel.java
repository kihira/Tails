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
import net.minecraft.util.EnumChatFormatting;

public class ControlsPanel extends Panel {

    private GuiButton partTypeButton;

    @SuppressWarnings("unchecked")
    public ControlsPanel(GuiEditor parent, int left, int top, int right, int bottom) {
        super(parent, left, top, right, bottom);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        //Reset/Save
        buttonList.add(new GuiButton(12, width - 83, height - 25, 40, 20, I18n.format("gui.button.reset")));
        buttonList.add(new GuiButton(13, width - 43, height - 25, 40, 20, I18n.format("gui.done")));

        //Export
        buttonList.add(new GuiBaseScreen.GuiButtonTooltip(14, (width / 2) - 20, height - 25, 40, 20, I18n.format("gui.button.export"),
                (width / 2) - 20, I18n.format("gui.button.export.0.tooltip")));

        //PartType Select
        buttonList.add(partTypeButton = new GuiButton(20, 3, height - 25, 40, 20, getParent().partType.name()));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawRect(0, 0, width, height, 0xDD000000);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        GuiEditor editor = (GuiEditor) parent;
        //Reset All
        if (button.id == 12) {
            editor.partInfo = editor.originalPartInfo.deepCopy();
            editor.selectDefaultListEntry();
            editor.setCurrTintEdit(0);
            editor.refreshTintPane();
            editor.updatePartsData();
        }
        //Save All
        else if (button.id == 13) {
            //Update part info, set local and send it to the server
            editor.updatePartsData();
            Tails.setLocalPartsData(editor.partsData);
            Tails.proxy.addPartsData(editor.partsData.uuid, editor.partsData);
            Tails.networkWrapper.sendToServer(new PlayerDataMessage(editor.partsData, false));
            ToastManager.INSTANCE.createCenteredToast(parent.width / 2, parent.height - 40, 100, EnumChatFormatting.GREEN + "Saved!");
            this.mc.displayGuiScreen(null);
        }
        //Export
        else if (button.id == 14) {
            editor.updatePartsData();
            mc.displayGuiScreen(new GuiExport(editor, editor.partsData));
        }
        //PartType
        else if (button.id == 20) {
            if (editor.partType.ordinal() + 1 >= PartsData.PartType.values().length) {
                editor.partType = PartsData.PartType.values()[0];
            }
            else {
                editor.partType = PartsData.PartType.values()[editor.partType.ordinal() + 1];
            }

            PartInfo newPartInfo = editor.partsData.getPartInfo(editor.partType);
            if (newPartInfo == null) {
                newPartInfo = new PartInfo(editor.partsData.uuid, false, 0, 0, 0, 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, null, editor.partType);
            }
            editor.originalPartInfo = newPartInfo.deepCopy();
            editor.partInfo = editor.originalPartInfo.deepCopy();
            editor.textureID = editor.partInfo.textureID;

            partTypeButton.displayString = editor.partType.name();
            editor.setCurrTintEdit(0);
            editor.initPartList();
            editor.refreshTintPane();
        }
    }

    private GuiEditor getParent() {
        return (GuiEditor) parent;
    }
}
